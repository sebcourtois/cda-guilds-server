package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class GuildsTimeMonitor implements Runnable {
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
                clientConnection.startDaemon();
                clientConnections.add(clientConnection);
            }
            clientConnections.forEach(GuildsTimeMonitorConnection::shutdown);
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
}


class GuildsTimeMonitorConnection implements Runnable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeMonitorConnection.class);

    private final Socket clientSocket;
    private Thread thread;
    private volatile boolean running;

    public GuildsTimeMonitorConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.running = false;
        this.thread = null;
    }

    public void listen() throws IOException {
        clientSocket.setSoTimeout(500);
        String hostName = clientSocket.getInetAddress().getHostName();
        LOG.info("{} listening to {}...", this.getClass().getSimpleName(), hostName);

        GuildsTimeClient gtClient = null;
        try {
            var outStream = new PrintStream(clientSocket.getOutputStream());
            gtClient = new GuildsTimeClient(outStream);
            gtClient.startDaemon();

            var inStream = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));

            String message;
            this.running = true;
            while (this.running) {
                try {
                    message = bufferedReader.readLine();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                if (message == null) {
                    LOG.info("{} disconnected.", hostName);
                    this.running = false;
                    continue;
                }
                outStream.printf("from %s: %s%n", hostName, message);
                switch (message.toLowerCase()) {
                    case "hello" -> gtClient.sayHello();
                    case "bye" -> gtClient.sayBye();
                    case "terminate" -> gtClient.sendMessage("{\"type\":\"terminate\"}");
                }
            }
        } finally {
            if (gtClient != null) gtClient.shutdown();
            if (!clientSocket.isClosed()) clientSocket.close();
            LOG.debug("{} stopped", this.getClass().getSimpleName());
        }
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

    public void startDaemon() {
        if (this.thread != null) throw new RuntimeException("daemon already started");
        this.thread = Thread.ofPlatform().daemon().start(this);
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