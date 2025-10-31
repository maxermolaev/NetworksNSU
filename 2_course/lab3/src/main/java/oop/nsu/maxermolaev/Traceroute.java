package oop.nsu.maxermolaev;

import java.net.*;

public class Traceroute {

    public static void run(String host) throws Exception {
        int port = 33434; // "dummy" порт
        int maxHops = 30;
        int timeout = 3000;

        System.out.println("\nTraceroute to " + host + ":");

        for (int ttl = 1; ttl <= maxHops; ttl++) {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(timeout);
                socket.setSoTimeout(ttl);

                byte[] buf = new byte[32];
                DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(host), port);
                socket.send(packet);

                DatagramPacket reply = new DatagramPacket(new byte[512], 512);
                socket.receive(reply);

                InetAddress routerAddress = reply.getAddress();
                System.out.println(ttl + ": " + routerAddress.getHostAddress());

                if (routerAddress.equals(InetAddress.getByName(host))) {
                    System.out.println("Reached destination.");
                    socket.close();
                    break;
                }

                socket.close();
            } catch (SocketTimeoutException e) {
                System.out.println(ttl + ": *");
            } catch (Exception e) {
                System.out.println(ttl + ": Error - " + e.getMessage());
            }
        }
    }
}
