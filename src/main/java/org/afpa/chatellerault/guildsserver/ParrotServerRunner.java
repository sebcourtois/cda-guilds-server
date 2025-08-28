package org.afpa.chatellerault.guildsserver;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

@Profile("!test")
//@org.springframework.stereotype.Component
public class ParrotServerRunner implements ApplicationRunner {
    private static final Logger LOG = LogManager.getLogger(ParrotServerRunner.class);
    private ServerSocket socket;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        int port = 50505;
        this.socket = new ServerSocket(port);
        LOG.info("Parrot server started on port {}", port);
        while (!this.socket.isClosed()) {
            try (Socket clientSocket = this.socket.accept()) {
                String hostName = clientSocket.getInetAddress().getHostName();
                LOG.info("Listening to {}...", hostName);

                var inputStream = clientSocket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                PrintStream out = new PrintStream(clientSocket.getOutputStream());
                while (!clientSocket.isClosed() && !this.socket.isClosed()) {
                    String message = bufferedReader.readLine();
                    if (message == null) {
                        LOG.info("Client disconnected.");
                        clientSocket.close();
                        continue;
                    }
                    out.printf("from %s: %s%n", hostName, message);
                }
            }
        }
        LOG.info("Server stopped");
    }

    @PreDestroy
    public void onExit() {
        try {
            LOG.info("closing server socket");
            this.socket.close();
        } catch (IOException e) {
            LOG.info("Failed to close server socket");
        }
    }
}
