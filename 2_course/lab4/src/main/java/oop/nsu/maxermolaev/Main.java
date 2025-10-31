package oop.nsu.maxermolaev;

import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Router A = new Router("A");
        Router B = new Router("B");
        Router C = new Router("C");

        Map<String, Router> network = new HashMap<>();
        network.put("A", A);
        network.put("B", B);
        network.put("C", C);

        A.addNeighbor(B, 1);
        B.addNeighbor(A, 1);

        A.addNeighbor(C, 1);
        C.addNeighbor(A, 1);

        B.addNeighbor(C, 1);
        C.addNeighbor(B, 1);

        A.startPeriodicUpdates(network);
        B.startPeriodicUpdates(network);
        C.startPeriodicUpdates(network);

        for (int i = 0; i < 5; i++) {
            Thread.sleep(6000);
            A.printRoutingTable();
            B.printRoutingTable();
            C.printRoutingTable();
        }

        System.exit(0);
    }
}
