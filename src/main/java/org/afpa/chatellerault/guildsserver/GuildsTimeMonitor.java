package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class GuildsTimeMonitor implements Runnable, Closeable {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeMonitor.class);

    private ServerSocket socket;
    private boolean running;

    public void start() throws IOException {
        int port = 50505;
        this.socket = new ServerSocket(port);
        this.socket.setSoTimeout(100);
        LOG.info("GuildsTimeMonitor started on port {}", port);
        this.running = true;

        Socket clientSocket;
        while (this.running) {
            try {
                clientSocket = this.socket.accept();
                clientSocket.setSoTimeout(100);
            } catch (SocketTimeoutException e) {
                continue;
            }
            String hostName = clientSocket.getInetAddress().getHostName();
            LOG.info("Listening to {}...", hostName);
            var inputStream = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            PrintStream out = new PrintStream(clientSocket.getOutputStream());

            var gtClient = new GuildsTimeClient(out);
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
                    LOG.info("Client disconnected.");
                    break;
                }
                out.printf("from %s: %s%n", hostName, message);
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
                LOG.info("Thread already interrupted", e);
            }
        }
        this.close();
        LOG.info("GuildsTimeMonitor stopped");
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
