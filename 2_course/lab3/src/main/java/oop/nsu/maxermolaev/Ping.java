package oop.nsu.maxermolaev;

import java.net.Socket;
import java.net.InetSocketAddress;

public class Ping {

    public static void run(String host, int count, int timeoutMs) {
        int received = 0;
        long totalTime = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        System.out.println("Pinging " + host + " with TCP SYN (pseudo-ICMP):");

        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, 80), timeoutMs);
                long end = System.nanoTime();

                long rtt = (end - start) / 1_000_000;
                System.out.println("Reply from " + host + ": time=" + rtt + "ms");

                received++;
                totalTime += rtt;
                min = Math.min(min, rtt);
                max = Math.max(max, rtt);

            } catch (Exception e) {
                System.out.println("Request timed out.");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n--- Ping statistics ---");
        System.out.printf("Packets: Sent = %d, Received = %d, Lost = %d (%d%% loss)\n",
                count, received, count - received, (count - received) * 100 / count);

        if (received > 0) {
            System.out.printf("Minimum = %dms, Maximum = %dms, Average = %.2fms\n",
                    min, max, (double) totalTime / received);
        }
    }
}
