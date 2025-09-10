package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public class SillyMulticastServer implements Runnable {
    private static final Logger LOG = LogManager.getLogger(SillyMulticastServer.class);

    @Override
    public void run() {
        int port = 5001;
        var socketAddress = new InetSocketAddress("228.5.6.7", port);
        try (MulticastSocket multicastSocket = new MulticastSocket(port)) {
            multicastSocket.setTimeToLive(3);
            multicastSocket.joinGroup(socketAddress, null);
            LOG.info("{} started on port {}",
                    this.getClass().getSimpleName(),
                    multicastSocket.getLocalPort()
            );

            String message = "rirififilouloudonalds";
            while (!multicastSocket.isClosed()) {
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socketAddress.getAddress(), port);
                multicastSocket.send(packet);
                LOG.info("message sent: {}", message);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
