package org.afpa.chatellerault.guildsserver;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class GuildsServer implements Runnable {
    private static final Logger LOG = LogManager.getLogger(GuildsServer.class);

    private ConnectionManager connMan;
    private Thread connManThread;
    private RequestManager reqMan;
    private Thread reqManThread;

    public void start() throws IOException {
        Collection<ClientConnection> clientConnections = Collections.synchronizedCollection(new ArrayList<>());

        this.connMan = new ConnectionManager(clientConnections);
        this.connManThread = Thread.ofPlatform().daemon().start(connMan);

        this.reqMan = new RequestManager(clientConnections);
        this.reqManThread = Thread.ofPlatform().daemon().start(reqMan);
    }

    public void stop() {
        this.reqMan.shutdown();
        try {
            this.reqManThread.join();
        } catch (InterruptedException e) {
            LOG.error(e);
        }

        this.connMan.shutdown();
        try {
            this.connManThread.join();
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        LOG.info("{} stopped", this.getClass().getSimpleName());
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

class ConnectionManager implements Runnable {
    private static final Logger LOG = LogManager.getLogger(ConnectionManager.class);

    private final ServerSocket socket;
    @Getter
    private final Collection<ClientConnection> clientConnections;

    ConnectionManager(Collection<ClientConnection> clientConnections) throws IOException {
        this.socket = new ServerSocket(50500);
        this.clientConnections = clientConnections;
    }

    public void listen() throws IOException {
        try (ServerSocket serverSocket = this.socket) {
            LOG.info("{} started on port {}", this.getClass().getSimpleName(), serverSocket.getLocalPort());
            Socket clientSocket;
            while (!serverSocket.isClosed()) {
                try {
                    clientSocket = serverSocket.accept();
                } catch (SocketException e) {
                    if (!serverSocket.isClosed()) LOG.error(e);
                    continue;
                }
                var client = new ClientConnection(clientSocket);
                synchronized (this.clientConnections) {
                    this.clientConnections.add(client);
                    LOG.info("Number of connections: {}", this.clientConnections.size());
                }
                LOG.info("{} listening to {} ...", this.getClass().getSimpleName(), client);
            }
        }
        clientConnections.forEach(ClientConnection::close);
        LOG.info("{} stopped", this.getClass().getSimpleName());
    }

    public void shutdown() {
        try {
            this.socket.close();
        } catch (IOException e) {
            LOG.info("failed to close {}'s socket", this.getClass().getSimpleName(), e);
        }
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

class ClientConnection implements Serializable {
    private static final Logger LOG = LogManager.getLogger(ClientConnection.class);

    @Getter
    private final Socket socket;
    @Getter
    private final BufferedReader reader;
    @Getter
    private final PrintWriter writer;

    ClientConnection(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public boolean isClosed() {
        return this.socket.isClosed();
    }

    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public String toString() {
        return "%s{%s}".formatted(this.getClass().getSimpleName(), this.socket);
    }
}

class RequestManager implements Runnable {
    private static final Logger LOG = LogManager.getLogger(RequestManager.class);

    private final Collection<ClientConnection> clientConnections;
    private volatile boolean running;

    RequestManager(Collection<ClientConnection> clientConnections) {
        this.clientConnections = clientConnections;
        this.running = false;
    }

    public void listen() {
        this.running = true;
        while (this.running) {
            synchronized (clientConnections) {
                for (ClientConnection client : clientConnections) {
                    try {
                        BufferedReader reader = client.getReader();
                        if (!reader.ready()) continue;
                        String request = reader.readLine();
                        System.out.println(request);
                    } catch (IOException e) {
                        LOG.error("Error reading from client: {}", client, e);
                    }
                }
            }
        }
        LOG.info("{} stopped", this.getClass().getSimpleName());
    }

    public void shutdown() {
        this.running = false;
    }

    @Override
    public void run() {
        this.listen();
    }
}