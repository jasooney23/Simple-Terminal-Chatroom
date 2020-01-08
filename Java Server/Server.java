import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.*;
import java.util.*;

/*

TODO:    > Multiple socket connections
            > Selectors
                > Server to client

*/

public class Server {
    private Socket clientSocket;
    private ServerSocket server;
    private PrintWriter outputStream;
    private Thread receiver;
    private Scanner lineInput;
    private Map<String, String> input;


    public Server(int port) {
        // Initialize things
        input = new HashMap<>();
        lineInput = new Scanner(System.in);
        input.put("command", "message");
        input.put("args", "hello world!");

        try {
            server = new ServerSocket(port);
            System.out.println(String.format("Server started on port %s", port));
        } catch (IllegalArgumentException exception) {
            System.out.println(exception);
        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    public void start() {
        while (true) {
            try {
                System.out.println("\nWaiting for a client");
                clientSocket = server.accept();
                System.out.println("\nClient accepted");
                outputStream = new PrintWriter(clientSocket.getOutputStream(), true);

                receiver = new Thread(new SocketReceiver(clientSocket, this));
                receiver.start();

                // Wait until receiver has disconnected with client
                while(receiver.isAlive()){
                    input.replace("command", "message");
                    input.replace("args", lineInput.nextLine());

                    sendJSON(outputStream, input);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnectClient(PrintWriter sender){
        Map<String, String> message = new HashMap<>();
        message.put("command", "disconnect");
        message.put("args", "goodbye world");
        try {
            sendJSON(sender, message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendJSON(PrintWriter sender, Map<String, String> message){
        JSONObject jsonMap = new JSONObject(message);

        if(jsonMap.getString("command") == "message")
            System.out.println(String.format(">>You: %s", jsonMap.getString("args")));

        sender.println(jsonMap.toString());
    }
}
