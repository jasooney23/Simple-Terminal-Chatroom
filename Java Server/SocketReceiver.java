import org.json.*;
import java.io.*;
import java.net.Socket;

class SocketReceiver implements Runnable{
    private Server server;
    private Socket clientSocket;
    private BufferedReader inputStream;
    private PrintWriter printWriter;

    private Boolean connected;
    private JSONObject receivedMessage;
    private String command;

    public SocketReceiver(Socket clientSocket, Server server){
        this.clientSocket = clientSocket;
        this.server = server;
        try{
            inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) ;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            connected = true;

            // Receives messages until client disconnects
            while (connected) {
                receivedMessage = new JSONObject(inputStream.readLine());
                command = receivedMessage.getString("command");

                // Decision maker
                switch (command) {
                    case "message":
                        System.out.println(String.format(">>Them: %s", receivedMessage.getString("args")));
                        break;
                    case "close":
                        // Disconnect
                        System.out.println("Client has disconnected");
                        server.disconnectClient(printWriter);
                        inputStream.close();
                        connected = false;
                        break;
                    default:
                        System.out.println("\nUnrecognized format\n");
                }
            }
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
