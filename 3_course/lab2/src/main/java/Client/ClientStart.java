package Client;

import java.io.File;
import java.io.IOException;

public class ClientStart {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("укажите путь до файла");
        }
        File file = new File(args[0]);
        Client client = new Client(file,1234);
    }
}
