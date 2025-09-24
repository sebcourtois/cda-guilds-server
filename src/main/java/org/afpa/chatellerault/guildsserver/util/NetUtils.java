package org.afpa.chatellerault.guildsserver.util;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;

public class NetUtils {

    public static void showIpConfig() throws SocketException {
        NetworkInterface.networkInterfaces()
                .filter(netItf -> !netItf.getInterfaceAddresses().isEmpty())
                .forEach(netItf ->
                        {
                            System.out.printf("%s%n", netItf);
                            netItf.getInterfaceAddresses().stream()
                                    .filter(itfAdr -> itfAdr.getAddress() instanceof Inet4Address)
                                    .forEach(
                                            itfAdr -> System.out.printf("    %s%n", itfAdr)
                                    );
                        }
                );
    }
}
