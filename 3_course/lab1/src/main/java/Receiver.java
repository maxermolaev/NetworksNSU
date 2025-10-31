import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.util.UUID;

public class Receiver implements Runnable {
    private volatile boolean running = true;
    private Multicast socket;
    private Info info;

    public Receiver(Multicast socket, Info info) {
        this.socket = socket;
        this.info = info;
    }

    @Override
    public void run() {
        while (running) {
            try {
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                String[] parts = new String(packet.getData()).split(";");
                String ip = parts[1];
                String uuid = parts[0];
                info.addNode(ip, UUID.fromString(uuid));
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
    }


}
