import org.xbill.DNS.ARecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.Section;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

public class Proxy {
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private Dns dnsResolver;

    public Proxy(int port) throws IOException {
        serverChannel = ServerSocketChannel.open().bind(new InetSocketAddress(port));
        selector = Selector.open();
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        DatagramChannel dnsChannel = DatagramChannel.open();
        dnsChannel.configureBlocking(false);
        dnsChannel.register(selector, SelectionKey.OP_READ);
        dnsResolver = new Dns(dnsChannel);

        System.out.println("SOCKS5 proxy started on port " + port);
    }

    public void start() throws IOException {
        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;

            var keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                try {
                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        clientAccept();
                    } else if (key.isReadable()) {
                        if (key.channel() instanceof DatagramChannel) {
                            readDnsAnswer(key);
                        } else {
                            readClient(key);
                        }
                    } else if (key.isConnectable()) {
                        connectClient(key);
                    }
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                    closeConnection(key);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                    closeConnection(key);
                }
            }
        }
    }


    private void clientAccept() throws IOException {
        SocketChannel client = serverChannel.accept();
        System.out.println("Accepted connection from " + client.getRemoteAddress());
        client.configureBlocking(false);
        ClientConfig config = new ClientConfig();
        config.setSocketChannel(client);
        client.register(selector, SelectionKey.OP_READ, config);
    }

    private void readClient(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientConfig config = (ClientConfig) key.attachment();

        if (config.getState() == ClientConfig.HANDSHAKE || config.getState() == ClientConfig.REQUEST) {
            ByteBuffer buffer = config.getClientData();
            int bytesRead = client.read(buffer);
            if (bytesRead <= 0) {
                closeConnection(key);
                return;
            }

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            buffer.clear();

            if (config.getState() == ClientConfig.HANDSHAKE) {
                handshakeHandler(data, config, client);
            } else if (config.getState() == ClientConfig.REQUEST) {
                requestHandler(data, config, client);
            }
        } else if (config.getState() == ClientConfig.RELAY) {
            relay(key);
        }
    }


    private void handshakeHandler(byte[] data, ClientConfig config, SocketChannel client) throws IOException {
        if (checkHandshake(data)) {
            byte[] response = new byte[]{Constants.VERSION, Constants.WOUTAUTH};
            client.write(ByteBuffer.wrap(response));
            System.out.println("Handshake completed with client " + client.getRemoteAddress());
            config.setState(ClientConfig.REQUEST);
        }
    }

    private boolean checkHandshake(byte[] data) {
        if (data[0] != Constants.VERSION) return false;
        int nMethods = data[1];
        for (int i = 2; i < 2 + nMethods; i++) {
            if (data[i] == Constants.WOUTAUTH) return true;
        }
        return false;
    }

    private void requestHandler(byte[] data, ClientConfig config, SocketChannel client) throws IOException {
        byte cmd = data[1];
        if (cmd != Constants.CMDCONNECT) {
            sendReply(client, Constants.CMDNOTSUPPORTED);
            throw new IllegalArgumentException("Command not supported");
        }

        byte atyp = data[3];
        String host;
        int port;

        if (atyp == Constants.IPv4) {
            host = (data[4] & 0xFF) + "." + (data[5] & 0xFF) + "." + (data[6] & 0xFF) + "." + (data[7] & 0xFF);
            port = ((data[8] & 0xFF) << 8) | (data[9] & 0xFF);
            System.out.println("CONNECT request to " + host + ":" + port);
            connectToRemote(host, port, config);
        } else if (atyp == Constants.DOMAIN) {
            int len = data[4];
            byte[] domainBytes = new byte[len];
            System.arraycopy(data, 5, domainBytes, 0, len);
            port = ((data[5 + len] & 0xFF) << 8) | (data[6 + len] & 0xFF);
            String domain = new String(domainBytes, StandardCharsets.UTF_8);
            System.out.println("CONNECT request to domain " + domain + ":" + port);
            SelectionKey key = client.keyFor(selector);
            dnsResolver.resolve(domainBytes, port, key);
        }
        else {
            sendReply(client, Constants.ADDRNOTSUPPORTED);
            throw new IllegalArgumentException("Address not supported");
        }
    }

    private void connectToRemote(String host, int port, ClientConfig config) throws IOException {
        SocketChannel remote = SocketChannel.open();
        remote.configureBlocking(false);
        remote.connect(new InetSocketAddress(host, port));
        config.setRemoteClient(remote);
        remote.register(selector, SelectionKey.OP_CONNECT, config);
    }

    private void connectClient(SelectionKey key) throws IOException {
        ClientConfig config = (ClientConfig) key.attachment();
        SocketChannel remote = (SocketChannel) key.channel();

        if (remote.finishConnect()) {
            System.out.println("Connected to remote server " + remote.getRemoteAddress());
            sendReply(config.getClientChannel(), Constants.SUCCEEDED);
            config.setState(ClientConfig.RELAY);

            remote.register(selector, SelectionKey.OP_READ, config);
            config.getClientChannel().register(selector, SelectionKey.OP_READ, config);
        }
    }

    private void relay(SelectionKey key) throws IOException {
        ClientConfig config = (ClientConfig) key.attachment();
        SocketChannel client = config.getClientChannel();
        SocketChannel remote = config.getRemoteClient();

        try {
            if (key.channel() == client && client.isOpen() && remote.isOpen()) {
                int bytesRead = client.read(config.getClientData());
                System.out.println("BytesRead client: " + bytesRead);
                if (bytesRead > 0) {
                    config.getClientData().flip();
                    while (config.getClientData().hasRemaining()) {
                        remote.write(config.getClientData());
                        System.out.println("Byteswrite client");
                    }
                    config.getClientData().clear();
                } else if (bytesRead == -1) {
                    closeConnection(key);
                    return;
                }
            } else if (key.channel() == remote && remote.isOpen() && client.isOpen()) {
                int bytesRead = remote.read(config.getRemoteData());
                System.out.println("BytesRead remote: " + bytesRead);
                if (bytesRead > 0) {
                    config.getRemoteData().flip();
                    while (config.getRemoteData().hasRemaining()) {
                        client.write(config.getRemoteData());
                        System.out.println("bytesWrite remote");
                    }
                    config.getRemoteData().clear();
                } else if (bytesRead == -1) {
                    closeConnection(key);
                }
            }
        } catch (IOException e) {
            closeConnection(key);
        }
    }



    private void readDnsAnswer(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        DatagramChannel channel = (DatagramChannel) key.channel();
        channel.receive(buffer);
        buffer.flip();

        if (buffer.remaining() == 0) return;

        Message message = new Message(buffer);
        int senderId = message.getHeader().getID();

        var match = dnsResolver.getClientMatch().remove(senderId);
        if (match == null) return;

        int port = match.getKey();
        SelectionKey clientKey = match.getValue();
        ClientConfig config = (ClientConfig) clientKey.attachment();
        SocketChannel client = config.getClientChannel();

        ARecord aRecord = message.getSection(Section.ANSWER).stream()
                .filter(it -> it instanceof ARecord)
                .map(it -> (ARecord) it)
                .findFirst()
                .orElse(null);

        if (aRecord == null) {
            System.err.println("No A record found for DNS response");
            sendReply(client, Constants.HOSTUNREACHABLE);
            client.close();
            return;
        }

        InetAddress ip = aRecord.getAddress();
        String ipString = ip.getHostAddress();
        connectToRemote(ipString, port, config);
    }

    private void sendReply(SocketChannel client, byte rep) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(10);
        buf.put(Constants.VERSION);
        buf.put(rep);
        buf.put(Constants.RSV);
        buf.put(Constants.IPv4);
        buf.put(new byte[]{0,0,0,0});
        buf.putShort((short)0);
        buf.flip();
        client.write(buf);
    }

    private void closeConnection(SelectionKey key) throws IOException {
       try {
           ClientConfig config = (ClientConfig) key.attachment();
           if (config != null) {
               if (config.getClientChannel() != null && config.getClientChannel().isOpen()) {
                   config.getClientChannel().close();
               }
               if (config.getRemoteClient() != null && config.getRemoteClient().isOpen()) {
                   config.getRemoteClient().close();
               }
           }
           key.cancel();
       }
       catch (IOException e) {
           throw new IOException("Error closing connection", e);
       }
       catch (Exception e) {
           throw new RuntimeException("Error closing connection", e);
       }
    }
}
