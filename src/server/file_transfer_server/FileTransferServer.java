package server.file_transfer_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FileTransferServer {
    private ServerSocket serverSocket;
    private static final int FILE_TRANSFER_PORT = 1338;
    public static final ConcurrentHashMap<String, FileTransferSession> activeTransfers = new ConcurrentHashMap<>();
    public static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        new FileTransferServer().startServer();
    }

    public void startServer() {
        establishConnection();
    }

    public void establishConnection() {
        try {
            serverSocket = new ServerSocket(FILE_TRANSFER_PORT);
            System.out.println("Server started on port " + FILE_TRANSFER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new FileTransferHandler(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error in establishing connection", e);
        }
    }
}