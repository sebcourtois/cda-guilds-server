package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.json.JsonParserFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Map;

public class GuildsTimeClient implements Runnable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeClient.class);
    private final int port;
    private final InetSocketAddress socketAddress;
    private final PrintStream printStream;
    private MulticastSocket socket;
    private volatile Thread thread;

    public GuildsTimeClient(PrintStream printStream) throws IOException {
        this.port = 5000;
        this.socketAddress = new InetSocketAddress("228.5.6.7", this.port);
        this.printStream = printStream;
        this.socket = null;
        this.thread = null;
    }

    @Override
    public void run() {
        try {
            this.socket = new MulticastSocket(this.port);
            LOG.info("{} started on port {}",
                    this.getClass().getSimpleName(),
                    this.socket.getLocalPort()
            );
            this.socket.setTimeToLive(3);
            this.socket.joinGroup(this.socketAddress, null);

            var jsonParser = JsonParserFactory.getJsonParser();
            byte[] buffer = new byte[1000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (!this.socket.isClosed()) {
                try {
                    this.socket.receive(packet);
                } catch (SocketException e) {
                    if (!this.socket.isClosed()) LOG.error(e);
                    break;
                } catch (IOException e) {
                    LOG.error("failed to receive packet from {}", this.socket, e);
                    continue;
                }
                var received = new String(packet.getData(), 0, packet.getLength());

                Map<String, Object> data;
                try {
                    data = jsonParser.parseMap(received);
                } catch (JsonParseException e) {
                    this.printStream.println(received);
                    continue;
                }
                String msgType = (String) data.get("type");
                if (msgType.equals("date")) {
                    this.printStream.println(data);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeSocket();
            LOG.info("{} stopped", this.getClass().getSimpleName());
        }
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
