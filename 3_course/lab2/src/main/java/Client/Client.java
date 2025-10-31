package Client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private int port;
    private String host = "localhost";
    private Socket socketClient;

    public Client(File file, int port) {
        this.port = port;;
        process(file);
    }

    private void process(File file) {
        try {
            try {
                socketClient = new Socket(host, port);
                System.out.println(socketClient.getLocalPort());
            }
            catch (IOException e) {
                throw new IOException("Could not connect to the server");
            }
            System.out.println("Connected to " + socketClient.getInetAddress().getHostAddress() + ":" + socketClient.getPort());
            checkFile(file);
            sendFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkFile(File file) throws IOException {
        if (file.getName().getBytes(StandardCharsets.UTF_8).length > 1024*4) {
            throw new IOException("File name is too big");
        }
        long maxSize = 1024L * 1024 * 1024 * 1024;
        if (file.length() > maxSize) {
            throw new IOException("File too large.");
        }
    }

    private void sendFile(File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(socketClient.getOutputStream());
        DataInputStream inFromServer = new DataInputStream(socketClient.getInputStream());

        String fileName = file.getName();
        byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(bytes.length);
        dos.write(bytes);

        dos.writeLong(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024*64];
            int bytesRead;
            while((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
        }

        int answer = inFromServer.readByte();
        if (answer == 1) {
            System.out.println("File has been sent.");
        }
        else {
            throw new IOException("File has not been sent.");
        }

    }

}
