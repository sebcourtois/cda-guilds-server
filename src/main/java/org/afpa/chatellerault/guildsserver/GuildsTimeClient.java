package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.json.JsonParserFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class GuildsTimeClient implements Runnable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeClient.class);
    private final int port;
    private final InetSocketAddress socketAddress;
    private final PrintStream printStream;
    private final MulticastSocket socket;
    private volatile boolean running;
    private Thread thread;

    public GuildsTimeClient(PrintStream printStream) throws IOException {
        this.port = 5000;
        this.socketAddress = new InetSocketAddress("228.5.6.7", this.port);
        this.socket = new MulticastSocket(port);
        this.socket.setSoTimeout(1000);
        this.socket.setTimeToLive(3);
        this.socket.joinGroup(socketAddress, null);
        this.printStream = printStream;
        this.running = false;
        this.thread = null;
    }

    public void listen() throws IOException {
        var jsonParser = JsonParserFactory.getJsonParser();
        byte[] buffer = new byte[1000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        this.running = true;
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

        if (!this.socket.isClosed()) {
            try {
                this.socket.leaveGroup(this.socketAddress, null);
            } catch (IOException e) {
                LOG.info("failed to leave multicast group", e);
            }
            this.socket.close();
        }
        LOG.debug("{} stopped", this.getClass().getSimpleName());
    }

    public void startDaemon() {
        if (this.thread != null) throw new RuntimeException("daemon already started");
        this.thread = Thread.ofPlatform().daemon().start(this);
    }

    public void shutdown() {
        this.running = false;
        if (this.thread != null) {
            try {
                this.thread.join(); // wait for GuildsTimeClient to stop
            } catch (InterruptedException e) {
                LOG.warn("{} thread already interrupted !?", this.getClass().getSimpleName());
            }
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

    @Override
    public void run() {
        try {
            this.listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
