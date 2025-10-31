import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientConfig {
    public static int HANDSHAKE = 1;
    public static int REQUEST = 2;
    public static int RELAY = 3;
    private SocketChannel client;
    private SocketChannel remoteClient;
    private ByteBuffer clientData;
    private ByteBuffer remoteData;
    private int state;

    public ClientConfig() {
        clientData = ByteBuffer.allocate(1024*16);
        remoteData = ByteBuffer.allocate(1024*16);
        state = HANDSHAKE;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.client = socketChannel;
    }

    public SocketChannel getClientChannel() {
        return client;
    }

    public void setRemoteClient(SocketChannel socketChannel) {
        this.remoteClient = socketChannel;
    }

    public SocketChannel getRemoteClient() {
        return remoteClient;
    }

    public ByteBuffer getClientData() {
        return clientData;
    }

    public ByteBuffer getRemoteData() {
        return remoteData;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}