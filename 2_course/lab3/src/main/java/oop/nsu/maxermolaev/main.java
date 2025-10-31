package oop.nsu.maxermolaev;

public class main {
    public static void main(String[] args) {
        String host = "google.com";
        int count = 4;
        int timeout = 1000;

        try {
            System.out.println("=== PING ===");
            Ping.run(host, count, timeout);

            System.out.println("\n=== TRACEROUTE ===");
            Traceroute.run(host);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
