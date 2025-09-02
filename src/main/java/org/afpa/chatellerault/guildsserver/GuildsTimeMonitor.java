package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class GuildsTimeMonitor implements Runnable, Closeable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeMonitor.class);

    private ServerSocket socket;
    private volatile boolean running;

    public GuildsTimeMonitor() {
        this.socket = null;
        this.running = false;
    }

    public void start() throws IOException {
        int port = 50505;
        this.socket = new ServerSocket(port);
        this.socket.setSoTimeout(1000);
        this.running = true;
        LOG.info("{} started on port {}", this.getClass().getSimpleName(),port);

        ArrayList<GuildsTimeMonitorConnection> gtClients = new ArrayList<>();
        Socket clientSocket;
        while (this.running) {
            try {
                clientSocket = this.socket.accept();
            } catch (SocketTimeoutException e) {
                continue;
            }
            var gtClient = new GuildsTimeMonitorConnection(clientSocket);
            gtClients.add(gtClient);
            new Thread(gtClient).start();
        }

        for (var gtClient : gtClients) {
            gtClient.stop();
        }
        this.close();
        LOG.info("{} stopped", this.getClass().getSimpleName());
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        try {
            this.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
        }
    }
}
