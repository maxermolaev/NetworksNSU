import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class Sender implements Runnable {
    private volatile boolean running = true;
    private final Multicast multicast;
    private final Nodes node;

    public Sender(Multicast multicast, Nodes node) {
        this.multicast = multicast;
        this.node = node;
    }

    @Override
    public void run() {
        while (running) {
            try {
                String msg = node.getUuid() + ";" + node.getIp();
                byte[] data = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, multicast.getGroup(), multicast.getPort());
                multicast.send(packet);
                Thread.sleep(2000);
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
