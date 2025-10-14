package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.model.GuildsDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.function.Consumer;

public class GuildsTimeClient implements Runnable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeClient.class);
    private static final JsonParser jsonParser = JsonParserFactory.getJsonParser();
    private final int port;
    private final InetSocketAddress socketAddress;
    private final MulticastSocket socket;
    private final Consumer<Map<String, Object>> dataConsumer;
    private volatile Thread thread;

    public GuildsTimeClient(@Nullable Consumer<Map<String, Object>> dataConsumer) throws IOException {
        this.dataConsumer = dataConsumer;
        this.port = 5000;
        this.socket = new MulticastSocket(this.port);
        this.socket.setTimeToLive(1);
        this.socketAddress = new InetSocketAddress("228.5.6.7", this.port);
        this.socket.joinGroup(this.socketAddress, null);
        this.thread = null;
    }

    @Override
    public void run() {
        LOG.info("{} listening on port {}",
                this.getClass().getSimpleName(),
                this.socket.getLocalPort()
        );
        while (!this.socket.isClosed()) {
            Map<String, Object> data;
            try {
                data = this.receive();
            } catch (SocketException e) {
                if (!this.socket.isClosed()) LOG.error(e);
                break;
            } catch (IOException e) {
                LOG.error("failed to receive packet from {}", this.socket, e);
                continue;
            }

            if (this.dataConsumer != null) {
                this.dataConsumer.accept(data);
            }
        }
        this.closeSocket();
        LOG.debug("{} no longer running", this.getClass().getSimpleName());
    }

    public Map<String, Object> receive() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(packet);
        var received = new String(packet.getData(), 0, packet.getLength());

        Map<String, Object> data;
        try {
            data = jsonParser.parseMap(received);
        } catch (JsonParseException e) {
            LOG.error("failed to parse received json data", e);
            data = Map.of("invalid_data", received);
        }
        return data;
    }

    public GuildsDate receiveDate() throws IOException {
        while (!this.socket.isClosed()) {
            Map<String, Object> data = this.receive();
            if (data.get("type").equals("date")) {
                return GuildsDate.builder()
                        .year((int) data.get("year"))
                        .day((int) data.get("day"))
                        .source((String) data.get("source"))
                        .build();
            }
        }
        return null;
    }

    public void start() {
        if (this.thread != null && !this.socket.isClosed()) {
            LOG.info("{} already running", this.getClass().getSimpleName());
            return;
        }
        this.thread = Thread.ofPlatform().start(this);
    }

    public void shutdown() {
        this.closeSocket();

        if (this.thread != null) {
            try {
                this.thread.join(); // wait for GuildsTimeClient to stop
            } catch (InterruptedException e) {
                LOG.warn("{} thread already interrupted !?", this.getClass().getSimpleName());
            }
        }
    }

    private void closeSocket() {
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.socket.leaveGroup(this.socketAddress, null);
            } catch (IOException e) {
                LOG.info("failed to leave multicast group", e);
            }
            this.socket.close();
        }
    }

    public void sayHello() throws IOException {
        String message = "{\"type\":\"hello\"}";
        this.sendMessage(message);
    }

    public void sayBye() throws IOException {
        String message = "{\"type\":\"bye\"}";
        this.sendMessage(message);
    }

    public void sendMessage(String message) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.socketAddress.getAddress(), this.port);
        this.socket.send(packet);
    }
}
