import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/*

TODO:    > Multiple socket connections
            > Selectors
                > Have server echo message to all clients (except sender)

*/

public class Server {
    private ServerSocketChannel server;
    private Selector selector;
    private List<SelectionKey> clientList;

    public Server(int port) throws Exception{
        // Initialize things
        server = ServerSocketChannel.open();
        selector = Selector.open();
        clientList = new ArrayList<SelectionKey>();

        server.bind(new InetSocketAddress("localhost", port));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println(String.format("Server started on port %s", port));
    }

    public void start() throws Exception{
        while (true) {
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    registerClient(server);
                }

                if(key.isReadable()){
                    String message = readFromChannel(key);
                    System.out.println(message);
                    sendToClients(key, message, clientList);
                }
                iterator.remove();
            }
        }
    }

    private void registerClient(ServerSocketChannel server) throws Exception{
        // Register Client
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        clientList.add(client.register(selector, SelectionKey.OP_READ));

        System.out.println("\nClient accepted");
    }

    private String readFromChannel(SelectionKey key) throws Exception{
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel clientChannel = (SocketChannel) key.channel();
        int channelRead = clientChannel.read(buffer);

        if(channelRead == -1){
            clientChannel.close();
            return "\n";
        }
        
        return new String(buffer.array());
    }

    private void sendToClients(SelectionKey clientToExclude, String message, List<SelectionKey> clientList) throws Exception{
        // Send to all clients in list, except for sender
        for(SelectionKey client : clientList){
            if(!client.equals(clientToExclude))
                sendToChannel(client, message, clientList);
        }
    }

    private void sendToChannel(SelectionKey key, String message, List<SelectionKey> clientList) throws Exception{
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel clientChannel = (SocketChannel) key.channel();

        if(!clientChannel.isConnected()){
            clientChannel.close();
            clientList.remove(key);
            return;
        }

        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        clientChannel.write(buffer);
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nPort to use:");
        int port = scanner.nextInt();

        Server server = new Server(port);
        server.start();
        
        scanner.close();
    }
}
