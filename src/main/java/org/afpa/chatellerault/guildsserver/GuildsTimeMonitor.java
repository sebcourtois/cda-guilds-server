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

    private volatile boolean running;

    public GuildsTimeMonitor() {
        this.running = false;
    }

    public void start() throws IOException {
        int port = 50505;
        ArrayList<GuildsTimeMonitorConnection> clientConnections = new ArrayList<>();

        try (var serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(1000);
            LOG.info("{} started on port {}", this.getClass().getSimpleName(), port);

            this.running = true;
            Socket clientSocket;
            while (this.running) {
                try {
                    clientSocket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                var clientConnection = new GuildsTimeMonitorConnection(clientSocket);
                clientConnections.add(clientConnection);
                clientConnection.startDaemon();
            }

            for (var conn : clientConnections) {
                conn.shutdown();
            }
        }
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
    public void close() {
        this.stop();
    }
}
