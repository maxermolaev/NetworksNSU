package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        File dir = new File("Uploads");
        dir.mkdir();
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Сервер запущен");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println(clientSocket.getLocalPort());
            ClientHandler client = new ClientHandler(clientSocket);
            Thread clientThread = new Thread(client);
            clientThread.start();
        }
    }
}