public class Main {
    public static void main(String[] args) throws Exception {
        String multicastIp = args[0];

        Multicast multicast = new Multicast(multicastIp);
        Nodes node = new Nodes();
        Info info = new Info();

        Sender sender = new Sender(multicast, node);
        Thread senderThread = new Thread(sender);
        senderThread.start();

        Receiver receiver = new Receiver(multicast, info);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();

        while (true) {
            info.removeDeadNodes(6000);
            info.printAliveNodes();
            Thread.sleep(3000);
        }
    }
}
