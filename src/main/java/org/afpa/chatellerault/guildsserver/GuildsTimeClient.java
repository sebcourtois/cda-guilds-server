package org.afpa.chatellerault.guildsserver;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.net.*;

@Profile("!test")
//@org.springframework.stereotype.Component
public class GuildsTimeClient implements ApplicationRunner {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeClient.class);

    public void run(ApplicationArguments args) throws Exception {

        LOG.info("HELLO");
        this.sayHelloToTimeServer();

        var jsonParser = JsonParserFactory.getJsonParser();
        int port = 5000;
        String multicastAddress = "228.5.6.7";
        var group = new InetSocketAddress(multicastAddress, port);
        var netInterface = NetworkInterface.getByName("bge0");
        try (var socket = new MulticastSocket(port)) {
            socket.setSoTimeout(1000);
            socket.joinGroup(group, netInterface);

            byte[] buffer = new byte[1000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            Long prevDay = null;
            long byeCount = 0;
            long helloCount = 0;
            int batchSize = 1;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException e) {
                    continue;
                }
                String received = new String(packet.getData(), 0, packet.getLength());
                var data = jsonParser.parseMap(received);
                String msgType = (String) data.get("type");
                if (msgType.equals("date")) {
                    long currDay = (long) data.get("day");
                    if (prevDay != null) {
                        System.out.printf("prev: %s - curr: %s %n", prevDay, currDay);
                        if (prevDay == currDay) {
                            if (byeCount > 0) LOG.info("{} BYE needed to get time to stop", byeCount);
//                            LOG.info("sending {} hello", batchSize);
//                            for (int i = 0; i < batchSize; i++) {
//                                this.sayHelloToTimeServer();
//                                helloCount++;
//                                Thread.sleep(10);
//                            }
//                            LOG.info("sent {} hello", helloCount);
                        } else if (prevDay < currDay) {
                            if (helloCount > 0) LOG.info("{} HELLO needed to get time to pass", helloCount);
                            LOG.info("sending {} bye", batchSize);
                            for (int i = 0; i < batchSize; i++) {
                                this.sayByeToTimeServer();
                                byeCount++;
                                Thread.sleep(10);
                            }
                            LOG.info("sent {} bye", byeCount);
                        }
                        System.out.printf("prev: %s - curr: %s %n", prevDay, currDay);
                    }
                    prevDay = currDay;
                    System.out.println(data);
                }
            }
        }
    }

    public void sayHelloToTimeServer() throws IOException {
        String message = "{\"type\":\"hello\"}";
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName("228.5.6.7");
        int port = 5000;
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);

        try (var socket = new MulticastSocket(5000)) {
            socket.send(packet);
        }
    }

    public void sayByeToTimeServer() throws IOException {
        String message = "{\"type\":\"bye\"}";
        byte[] buffer = message.getBytes();
        InetAddress group = InetAddress.getByName("228.5.6.7");
        int port = 5000;
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);

        try (var socket = new MulticastSocket(5000)) {
            socket.send(packet);
        }
    }

    @PreDestroy
    public void onExit() {
        try {
            this.sayByeToTimeServer();
            LOG.info("BYE");
        } catch (IOException e) {
            LOG.info("failed to say bye to time server");
        }
    }

}
