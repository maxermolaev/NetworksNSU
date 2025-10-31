package oop.nsu.maxermolaev;

public class RoutingTableEntry {
    public final String destination;
    public int cost;
    public String nextHop;
    public long lastUpdated;

    public RoutingTableEntry(String destination, int cost, String nextHop) {
        this.destination = destination;
        this.cost = cost;
        this.nextHop = nextHop;
        this.lastUpdated = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format("To: %s | Cost: %d | Next hop: %s", destination, cost, nextHop);
    }
}

