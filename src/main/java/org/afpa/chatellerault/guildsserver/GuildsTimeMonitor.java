package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class GuildsTimeMonitor implements Runnable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeMonitor.class);

    private ServerSocket serverSocket;

    public GuildsTimeMonitor() {
        this.serverSocket = null;
    }

    @Override
    public void run() {
        int port = 50505;
        ArrayList<GuildsTimeMonitorConnection> clientConnections = new ArrayList<>();

        try (var socket = new ServerSocket(port)) {
            this.serverSocket = socket;
            LOG.info("{} started on port {}", this.getClass().getSimpleName(), port);

            Socket clientSocket;
            while (!this.serverSocket.isClosed()) {
                try {
                    clientSocket = serverSocket.accept();
                } catch (SocketException e) {
                    if (!this.serverSocket.isClosed()) LOG.error(e);
                    break;
                }
                var conn = new GuildsTimeMonitorConnection(clientSocket);
                conn.start();
                clientConnections.add(conn);
            }
            clientConnections.forEach(GuildsTimeMonitorConnection::shutdown);
        } catch (IOException e) {
            LOG.error("failed to create server socket", e);
        }
        LOG.info("{} stopped", this.getClass().getSimpleName());
    }

    public void stop() {
        try {
            if (!serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            LOG.info("failed to close client socket", e);
        }
    }
}


class GuildsTimeMonitorConnection implements Runnable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeMonitorConnection.class);

    private final Socket clientSocket;
    private volatile Thread thread;

    public GuildsTimeMonitorConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.thread = null;
    }

    @Override
    public void run() {
        String hostName = clientSocket.getInetAddress().toString();
        LOG.info("{} listening to {}...", this.getClass().getSimpleName(), hostName);

        GuildsTimeClient gtClient = null;
        PrintStream outStream = null;
        InputStream inStream = null;
        try {
            outStream = new PrintStream(clientSocket.getOutputStream());
            gtClient = new GuildsTimeClient(outStream);
            gtClient.start();

            inStream = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));

            String message;
            while (!this.clientSocket.isClosed()) {
                try {
                    message = bufferedReader.readLine();
                } catch (SocketException e) {
                    if (!this.clientSocket.isClosed()) LOG.error(e);
                    break;
                }
                if (message == null) {
                    LOG.info("{} disconnected.", hostName);
                    break;
                }
                outStream.printf("sending: %s...%n", message);
                switch (message.toLowerCase()) {
                    case "hello" -> gtClient.sayHello();
                    case "bye" -> gtClient.sayBye();
                    case "terminate" -> gtClient.sendMessage("{\"type\":\"terminate\"}");
                }
            }
        } catch (IOException e) {
            LOG.error("failed reading from {}", this.clientSocket, e);
        } finally {
            if (gtClient != null) gtClient.shutdown();
            if (outStream != null) outStream.close();
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    LOG.info("failed to close input stream", e);
                }
            }
            this.closeSocket();
            LOG.info("{} stopped listening to {}", this.getClass().getSimpleName(), hostName);
        }
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
        try {
            if (!clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            LOG.info("failed to close client socket", e);
        }
    }

    public void start() {
        if (this.thread != null && !this.clientSocket.isClosed()) {
            LOG.info("{} already running", this.getClass().getSimpleName());
            return;
        }
        this.thread = Thread.ofPlatform().start(this);
    }
}