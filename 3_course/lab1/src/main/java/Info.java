import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Info {
    private final List<Nodes> nodes = new ArrayList<>();

    public void addNode(String ip, UUID uuid) {
        for (Nodes n : nodes) {
            if (n.getUuid().equals(uuid)) {
                n.updateLastSeen();
                return;
            }
        }
        nodes.add(new Nodes(ip, uuid));
    }

    public void removeDeadNodes(long timeoutMs) {
        long now = System.currentTimeMillis();
        Iterator<Nodes> it = nodes.iterator();
        while (it.hasNext()) {
            Nodes n = it.next();
            if (now - n.getLastSeen() > timeoutMs) {
                System.out.println("Узел " + n.getUuid());
                it.remove();
            }
        }
    }

    public void printAliveNodes() {
        if (nodes.isEmpty()) {
            System.out.println("Живых узлов нет");
        } else {
            System.out.println("Живые узлы:");
            for (Nodes n : nodes) {
                System.out.println("  UUID=" + n.getUuid());
            }
        }
    }
}
