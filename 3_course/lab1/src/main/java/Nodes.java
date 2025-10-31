import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class Nodes {
    private String nodeIp;
    private UUID uuid;
    private long lastseen;

    public Nodes(String nodeIp, UUID uuid) {
        this.nodeIp = nodeIp;
        this.uuid = uuid;
        this.lastseen = System.currentTimeMillis();
    }

    public Nodes() throws UnknownHostException {
        this.uuid = UUID.randomUUID();
        this.nodeIp = InetAddress.getLocalHost().getHostAddress();
    }

    public void updateLastSeen() {
        this.lastseen = System.currentTimeMillis();
    }

    public long getLastSeen() {
        return lastseen;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getIp() {
        return nodeIp;
    }
}
