import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nPort to use:");
        int port = scanner.nextInt();
        Server server = new Server(port);
        server.start();
        scanner.close();
    }
}
