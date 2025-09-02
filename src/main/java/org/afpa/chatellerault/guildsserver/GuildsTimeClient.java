package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.json.JsonParserFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class GuildsTimeClient implements Runnable, Closeable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeClient.class);
    private final int port;
    private final InetSocketAddress socketAddress;
    private final PrintStream printStream;
    private final MulticastSocket socket;
    private volatile boolean running;

    public GuildsTimeClient(PrintStream printStream) throws IOException {
        this.port = 5000;
        this.socketAddress = new InetSocketAddress("228.5.6.7", this.port);
        this.socket = new MulticastSocket(port);
        this.socket.setSoTimeout(1000);
        this.socket.joinGroup(socketAddress, null);
        this.printStream = printStream;
        this.running = true;
    }

    public void start() throws IOException {
        this.running = true;
        var jsonParser = JsonParserFactory.getJsonParser();

        byte[] buffer = new byte[1000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (this.running) {
            try {
                this.socket.receive(packet);
            } catch (SocketTimeoutException e) {
                continue;
            }
            var received = new String(packet.getData(), 0, packet.getLength());
            var data = jsonParser.parseMap(received);
            String msgType = (String) data.get("type");
            if (msgType.equals("date")) {
                this.printStream.println(data);
            }
        }
        this.close();
        LOG.info("{} stopped", this.getClass().getSimpleName());
    }

    public void stop() {
        this.running = false;
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

    @Override
    public void close() {
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.socket.leaveGroup(this.socketAddress, null);
            } catch (IOException e) {
                LOG.info("failed to leave multicast group", e);
            }
            this.socket.close();
        }
    }

    @Override
    public void run() {
        try {
            this.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
