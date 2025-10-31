package oop.nsu.maxermolaev;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class PortScanner {
    private static final int TIMEOUT = 500;
    private static final Map<Integer, String> serviceMap = new HashMap<>();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int startPort = 1;
        int endPort = 1024;
        String host = "127.0.0.1";

        loadServices("/etc/services");

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Future<String>> results = new ArrayList<>();

        for (int port = startPort; port <= endPort; port++) {
            final int currentPort = port;
            results.add(executor.submit(() -> scanPort(host, currentPort)));
        }

        for (Future<String> result : results) {
            String output = result.get();
            if (!output.isEmpty()) {
                System.out.println(output);
            }
        }

        executor.shutdown();
    }

    private static String scanPort(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), TIMEOUT);
            String service = serviceMap.getOrDefault(port, "неизвестно");
            return "Открыт порт: " + port + " (" + service + ")";
        } catch (IOException e) {
            return "Закрыт порт: " + port;
        }
    }


    private static void loadServices(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length >= 2 && parts[1].contains("/tcp")) {
                    String serviceName = parts[0];
                    String portPart = parts[1].split("/")[0];
                    try {
                        int port = Integer.parseInt(portPart);
                        serviceMap.put(port, serviceName);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки /etc/services: " + e.getMessage());
        }
    }
}

