package oop.nsu.maxermolaev;

import java.util.*;
import java.util.concurrent.*;

public class Router {
    private static final int INFINITY = 16;
    private static final long TIMEOUT_MS = 15000;

    private final String name;
    private final Map<String, Integer> neighbors;
    private final Map<String, RoutingTableEntry> routingTable;
    private final ScheduledExecutorService scheduler;

    public Router(String name) {
        this.name = name;
        this.neighbors = new HashMap<>();
        this.routingTable = new HashMap<>();
        this.routingTable.put(name, new RoutingTableEntry(name, 0, name));
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public String getName() {
        return name;
    }

    public void addNeighbor(Router neighbor, int cost) {
        neighbors.put(neighbor.getName(), cost);
        routingTable.put(neighbor.getName(), new RoutingTableEntry(neighbor.getName(), cost, neighbor.getName()));
    }

    public void receiveTable(String from, Map<String, RoutingTableEntry> receivedTable) {
        int costToNeighbor = neighbors.getOrDefault(from, INFINITY);

        for (RoutingTableEntry entry : receivedTable.values()) {
            if (entry.destination.equals(this.name)) continue;

            int newCost = Math.min(INFINITY, entry.cost + costToNeighbor);
            RoutingTableEntry current = routingTable.get(entry.destination);

            if (current == null || newCost < current.cost || current.nextHop.equals(from)) {
                routingTable.put(entry.destination, new RoutingTableEntry(entry.destination, newCost, from));
            }
        }
    }

    public void startPeriodicUpdates(Map<String, Router> network) {
        scheduler.scheduleAtFixedRate(() -> {
            expireOldRoutes();
            broadcastTable(network);
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void expireOldRoutes() {
        long now = System.currentTimeMillis();
        for (RoutingTableEntry entry : routingTable.values()) {
            if (!entry.destination.equals(name) && (now - entry.lastUpdated > TIMEOUT_MS)) {
                entry.cost = INFINITY;
            }
        }
    }

    private void broadcastTable(Map<String, Router> network) {
        for (String neighborName : neighbors.keySet()) {
            Router neighbor = network.get(neighborName);

            Map<String, RoutingTableEntry> splitHorizonTable = new HashMap<>();
            for (RoutingTableEntry entry : routingTable.values()) {
                if (!entry.nextHop.equals(neighborName)) {
                    splitHorizonTable.put(entry.destination, new RoutingTableEntry(
                            entry.destination, entry.cost, entry.nextHop
                    ));
                }
            }

            neighbor.receiveTable(name, splitHorizonTable);
        }
    }

    public void printRoutingTable() {
        System.out.println("==== Таблица маршрутизации " + name + " ====");
        for (RoutingTableEntry entry : routingTable.values()) {
            System.out.println(entry);
        }
        System.out.println();
    }
}
