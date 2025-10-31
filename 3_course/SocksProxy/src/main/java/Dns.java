import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class Dns {
    public static final int DNS_PORT = 53;
    private static final String DNS_SERVER = "8.8.8.8";

    private final DatagramChannel dnsChannel;
    private final InetSocketAddress dnsServerAddress;
    private int senderID = 0;

    private final Map<Integer, Map.Entry<Integer, SelectionKey>> clientMatch = new HashMap<>();

    public Dns(DatagramChannel dnsChannel) {
        this.dnsChannel = dnsChannel;
        this.dnsServerAddress = new InetSocketAddress(DNS_SERVER, DNS_PORT);
    }

    public Map<Integer, Map.Entry<Integer, SelectionKey>> getClientMatch() {
        return clientMatch;
    }

    public void resolve(byte[] addr, int port, SelectionKey key) {
        try {
            String domain = new String(addr, StandardCharsets.UTF_8);
            if (!domain.endsWith(".")) domain += ".";

            Message message = new Message();
            Record record =
                   Record.newRecord(Name.fromString(domain), Type.A, DClass.IN);
            message.addRecord(record, Section.QUESTION);

            Header header = message.getHeader();
            header.setFlag(Flags.RD);
            header.setID(senderID & 0xFFFF);

            clientMatch.put(senderID, new AbstractMap.SimpleEntry<>(port, key));

            byte[] out = message.toWire();
            dnsChannel.send(ByteBuffer.wrap(out), dnsServerAddress);

            senderID = (senderID + 1) & 0xFFFF;

            System.out.println("DNS request sent for " + domain + " (id=" + senderID + ")");
        } catch (Exception exc) {
            throw new IllegalArgumentException("DNS resolve failed: ", exc);
        }
    }


}