import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Proxy proxy = new Proxy(1080);
        proxy.start();
    }
}
