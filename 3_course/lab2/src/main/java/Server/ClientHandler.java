package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            getFile(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getFile(Socket clientSocket) throws IOException {
        double start = System.nanoTime();
        double lastTime = start;
        long BytesLastSeen = 0;

        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
        int nameLenght = in.readInt();
        byte[] name = new byte[nameLenght];
        in.readFully(name);

        String filename = new String(name, StandardCharsets.UTF_8);

        long fileSize = in.readLong();

        try (FileOutputStream out = new FileOutputStream("Uploads/" + filename)) {
            int total = 0;
            byte[] buffer = new byte[64*1024];
            int bytesRead;
            while (total < fileSize && (bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                total += bytesRead;
                BytesLastSeen += bytesRead;

                double now = System.nanoTime();
                double reportTime = (now - lastTime) / 1_000_000_000;

                if (reportTime > 3.0) {
                    double instantSpeed = BytesLastSeen / reportTime;
                    double avgSpeed = total / ((now - start) / 1_000_000_000);

                    lastTime = now;
                    BytesLastSeen = 0;
                    System.out.println("Скорость за 3 секунды: " + instantSpeed + "B/s" + " Средняя скорость: " + avgSpeed + "B/s");
                }
            }
            double endTime = System.nanoTime();
            double totalTime = (endTime - start) / 1_000_000_000.0;
            if (totalTime < 3.0) {;
                double avgBytes = total / totalTime;
                System.out.println(totalTime);
                System.out.printf("Средняя скорость: %.2f KB/s%n", avgBytes / 1024);
            }

            if (total == fileSize) {
                outToClient.write(1);
            }
            else {
                outToClient.write(0);
                throw new IOException("Размеры файлов не совпадают");
            }
            outToClient.flush();
        }
    }
}
