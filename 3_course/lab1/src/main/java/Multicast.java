import java.io.IOException;
import java.net.*;

public class Multicast {
    private final MulticastSocket socket;
    private final InetAddress group;
    private final int port = 1234;
    private NetworkInterface networkInterface;

    public Multicast(String ip) throws IOException {
        this.group = InetAddress.getByName(ip);
        this.socket = new MulticastSocket(port);

        if (group instanceof Inet6Address) {
            networkInterface = NetworkInterface.getByName("en0");
            if (networkInterface == null) {
                throw new IOException("Интерфейс en0 не найден");
            }

            socket.setNetworkInterface(networkInterface);
            socket.joinGroup(new InetSocketAddress(group, port), networkInterface);
        } else {
            socket.joinGroup(group);
        }
    }

    public InetAddress getGroup() { return group; }
    public int getPort() { return port; }

    public void send(DatagramPacket packet) throws IOException { socket.send(packet); }
    public void receive(DatagramPacket packet) throws IOException { socket.receive(packet); }
}
