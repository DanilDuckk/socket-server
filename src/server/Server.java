package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int SERVER_PORT = 1337;
    private ServerSocket serverSocket;
    public static RPSManager rpsManager = new RPSManager();

    public static void main(String[] args) {
        new Server().startServer();
    }

    public void startServer() {
        establishConnection();
    }

    public void establishConnection() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started on port " + SERVER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error in establishing connection", e);
        }
    }

}
