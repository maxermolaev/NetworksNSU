package Networks.maxermolaev;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

class SimpleHttpServer {

    private static final int PORT = 80;
    private static final String BASE_DIR = "static";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on http://localhost:" + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleClient(clientSocket);
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            String line = in.readLine();
            if (line == null || line.isEmpty()) return;

            StringTokenizer tokens = new StringTokenizer(line);
            String method = tokens.nextToken();
            String path = tokens.nextToken();

            if (path.equals("/")) path = "/index.html";
            File file = new File(BASE_DIR + path);
            System.out.println("Ищу файл по пути: " + file.getAbsolutePath());

            if (method.equals("GET")) {
                if (file.exists() && file.isFile()) {
                    byte[] content = Files.readAllBytes(file.toPath());
                    String mimeType = Files.probeContentType(file.toPath());
                    writeResponse(out, 200, "OK", mimeType, content);
                } else {
                    byte[] content = "404 Not Found".getBytes();
                    writeResponse(out, 404, "Not Found", "text/plain", content);
                }
            } else if (method.equals("POST")) {
                int contentLength = 0;
                String header;
                while (!(header = in.readLine()).isEmpty()) {
                    if (header.toLowerCase().startsWith("content-length:")) {
                        contentLength = Integer.parseInt(header.split(":")[1].trim());
                    }
                }

                char[] bodyChars = new char[contentLength];
                in.read(bodyChars);
                String body = new String(bodyChars);

                try (FileWriter writer = new FileWriter("data.txt", true)) {
                    writer.write(body + "\n");
                }

                byte[] content = "Data saved.".getBytes();
                writeResponse(out, 200, "OK", "text/plain", content);
            }
            else {
                byte[] content = "405 Method Not Allowed".getBytes();
                writeResponse(out, 405, "Method Not Allowed", "text/plain", content);
            }

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeResponse(OutputStream out, int statusCode, String statusText, String contentType, byte[] content) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        writer.print("HTTP/1.1 " + statusCode + " " + statusText + "\r\n");
        writer.print("Content-Type: " + contentType + "\r\n");
        writer.print("Content-Length: " + content.length + "\r\n");
        writer.print("Connection: close\r\n");
        writer.print("\r\n");
        writer.flush();
        out.write(content);
        out.flush();
    }
}
