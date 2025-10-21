package org.afpa.chatellerault.guildsserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.afpa.chatellerault.guildsserver.core.RequestCommand;
import org.afpa.chatellerault.guildsserver.core.RequestCommands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;


public class GuildsServer implements Runnable {
    private static final Logger LOG = LogManager.getLogger(GuildsServer.class);
    private final Collection<ClientConnection> clientConnections;
    private final RequestManager requestMan;
    private ServerSocket socket;
    private volatile Thread thread;
    private volatile boolean requestManStarted;

    GuildsServer() {
        this.socket = null;
        this.clientConnections = Collections.synchronizedCollection(new ArrayList<>());
        this.requestMan = new RequestManager(clientConnections);
        this.requestManStarted = false;
        this.thread = null;
    }

    public void start() {
        if (this.thread != null && !this.socket.isClosed()) {
            LOG.info("{} already running", this.getClass().getSimpleName());
            return;
        }
        this.thread = Thread.ofPlatform().start(this);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(49394)) {
            this.socket = serverSocket;
            LOG.info("{} started on port {}",
                    this.getClass().getSimpleName(),
                    serverSocket.getLocalPort()
            );
            Socket clientSocket;
            while (!serverSocket.isClosed()) {
                try {
                    clientSocket = serverSocket.accept();
                } catch (SocketException e) {
                    if (!serverSocket.isClosed()) LOG.error(e);
                    continue;
                }
                synchronized (this.clientConnections) {
                    this.clientConnections.removeIf(ClientConnection::isClosed);
                    this.closeDisconnectedSockets();

                    var client = new ClientConnection(clientSocket);
                    this.clientConnections.add(client);
                    LOG.info("{} listening to {} ...", this.getClass().getSimpleName(), client);
                    LOG.info("Number of connections: {}", this.clientConnections.size());
                    if (!this.requestManStarted && !this.clientConnections.isEmpty()) {
                        this.requestMan.start();
                        this.requestManStarted = true;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clientConnections.forEach(ClientConnection::close);
        LOG.debug("{} no longer running", this.getClass().getSimpleName());
    }

    private void closeDisconnectedSockets() {
        for (var client : this.clientConnections) {
            if (client.isClosed()) continue;
            if (!client.isConnected()) {
                LOG.info("Client disconnected: {}", client);
                client.close();
            }
        }
    }

    public void shutdown() {
        this.requestMan.shutdown();
        this.clientConnections.forEach(ClientConnection::close);
        try {
            this.closeSocket();
        } catch (IOException e) {
            LOG.info("failed to close {}'s socket", this.getClass().getSimpleName(), e);
        }

        if (this.thread != null) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                LOG.info("failed to wait for {}'s thread", this.getClass().getSimpleName(), e);
            }
        }

        LOG.info("{} shutdown done", this.getClass().getSimpleName());
    }


    public boolean isRunning() {
        return (this.socket != null && !this.socket.isClosed());
    }

    public void closeSocket() throws IOException {
        if (this.socket != null && !this.socket.isClosed()) this.socket.close();
    }

}

class RequestManager implements Runnable {
    private static final Logger LOG = LogManager.getLogger(RequestManager.class);

    private final Collection<ClientConnection> clientConnections;
    private volatile boolean running;
    private volatile Thread thread;

    RequestManager(Collection<ClientConnection> clientConnections) {
        this.clientConnections = clientConnections;
        this.running = false;
        this.thread = null;
    }

    @Override
    public void run() {
        LOG.info("Listening to client requests...");
        this.running = true;
        while (this.running) {
            synchronized (clientConnections) {
                for (ClientConnection client : clientConnections) {
                    if (!this.running) break;
                    if (client.isClosed()) continue;
                    try {
                        BufferedReader reader = client.getReader();
                        if (!reader.ready()) continue;
                        String request = reader.readLine();
                        Thread.ofVirtual().start(
                                new RequestRunner(client.getWriter(), request)
                        );
                    } catch (IOException e) {
                        LOG.error("Error reading from client: {}", client, e);
                    }
                }
            }
        }
        LOG.debug("{} no longer running", this.getClass().getSimpleName());
    }

    public void start() {
        if (this.thread != null && this.running) {
            LOG.info("{} already running", this.getClass().getSimpleName());
            return;
        }
        this.thread = Thread.ofPlatform().start(this);
    }

    public void shutdown() {
        this.running = false;

        if (this.thread != null) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                LOG.warn("fail to wait for {}'s thread", this.getClass().getSimpleName(), e);
            }
        }
        LOG.info("{} shutdown done", this.getClass().getSimpleName());
    }
}

class SimpleEchoCmd implements RequestCommand {
    private String message;

    public SimpleEchoCmd(String message) {
        this.message = message;
    }

    public SimpleEchoCmd() {
    }

    public void loadParams(JsonNode paramsNode) {
        this.message = paramsNode.get("message").asText();
    }

    public String execute() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this.message;
    }
}

@Log4j2
class RequestRunner implements Runnable {
    private static final com.fasterxml.jackson.databind.ObjectMapper
            jsonMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    private final PrintWriter writer;
    private final String request;


    RequestRunner(PrintWriter writer, String request) {
        this.writer = writer;
        this.request = request;
    }

    private static String executeRequest(String request) {
        JsonNode requestRoot;
        try {
            requestRoot = jsonMapper.readTree(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid request: Not a proper json format", e);
        }

        JsonNode commandNode = requestRoot.get("command");
        if (commandNode == null) {
            throw new RuntimeException("Invalid request: 'command' key missing");
        }
        String commandKey = commandNode.asText();
        RequestCommand command = RequestCommands.get(commandKey);
        JsonNode params = requestRoot.get("params");
        try {
            command.loadParams(params);
        } catch (Exception e) {
            throw new RuntimeException(
                    "failed to load params for commands: '%s'".formatted(commandKey), e
            );
        }

        String result;
        try {
            result = command.execute();
        } catch (Exception e) {
            throw new RuntimeException(
                    "failed to execute requested command: '%s'".formatted(commandKey), e
            );
        }
        return result;
    }

    @Override
    public void run() {
        String result = null;
        String errorMsg = null;
        try {
            result = executeRequest(this.request);
        } catch (Exception e) {
            errorMsg = e.toString();
            log.error(e);
        }

        String response = null;
        if (result != null) {
            try {
                response = jsonMapper.writeValueAsString(Map.of("result", result));
            } catch (JsonProcessingException e) {
                errorMsg = e.toString();
                log.error(e);
            }
        }
        if (errorMsg != null) {
            try {
                response = jsonMapper.writeValueAsString(Map.of("error", errorMsg));
            } catch (JsonProcessingException e) {
                log.error(e);
            }
        }
        this.writer.println(response);
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

    public boolean isConnected() {
        boolean connected = true;
        try {
            if (this.reader.ready()) return true;

            this.socket.setSoTimeout(1000);
            try {
                connected = (reader.readLine() != null);
            } catch (SocketTimeoutException ignored) {
            } finally {
                if (!this.socket.isClosed()) this.socket.setSoTimeout(0);
            }
        } catch (SocketException e) {
            connected = false;
        } catch (IOException e) {
            LOG.error("Failed to check if client still connected. Assuming it is still connected...", e);
        }
        return connected;
    }

    public void close() {
        List<Closeable> thingsToClose = List.of(this.reader, this.writer, this.socket);
        for (var each : thingsToClose) {
            try {
                each.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    public String hostName() {
        return this.socket.getInetAddress().toString();
    }

    @Override
    public String toString() {
        return "%s{%s}".formatted(this.hostName(), this.socket);
    }
}
