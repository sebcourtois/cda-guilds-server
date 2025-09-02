package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class GuildsTimeMonitorConnection implements Runnable, Closeable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeMonitorConnection.class);

    private final Socket clientSocket;
    private volatile boolean running;

    public GuildsTimeMonitorConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.running = false;
    }

    public void start() throws IOException {
        this.running = true;
        clientSocket.setSoTimeout(500);

        String hostName = clientSocket.getInetAddress().getHostName();
        LOG.info("Listening to {}...", hostName);

        var inStream = clientSocket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
        PrintStream outStream = new PrintStream(clientSocket.getOutputStream());

        var gtClient = new GuildsTimeClient(outStream);
        var gtcThread = new Thread(gtClient);
        gtcThread.start();

        String message;
        while (this.running) {
            try {
                message = bufferedReader.readLine();
            } catch (SocketTimeoutException e) {
                continue;
            }
            if (message == null) {
                LOG.info("{} disconnected.", hostName);
                break;
            }
            outStream.printf("from %s: %s%n", hostName, message);
            switch (message.toLowerCase()) {
                case "hello" -> gtClient.sayHello();
                case "bye" -> gtClient.sayBye();
                case "terminate" -> gtClient.sendMessage("{\"type\":\"terminate\"}");
            }
        }

        gtClient.stop();
        try {
            gtcThread.join();
        } catch (InterruptedException e) {
            LOG.info("{} thread already interrupted", gtClient.getClass().getSimpleName());
        }
        this.close();
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
        if (this.clientSocket != null && !this.clientSocket.isClosed()) {
            this.clientSocket.close();
        }
    }
}
