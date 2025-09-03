package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class GuildsTimeMonitorConnection implements Runnable {
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
        this.thread = Thread.ofVirtual().start(this);
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
