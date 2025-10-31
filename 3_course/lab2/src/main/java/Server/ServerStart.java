package Server;

import java.io.IOException;

public class ServerStart {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java ServerStart <port>");
        }
        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
        server.start();
    }
}
