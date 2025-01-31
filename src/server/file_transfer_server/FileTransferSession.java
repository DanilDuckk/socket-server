package server.file_transfer_server;

import java.net.Socket;

public class FileTransferSession {
    private Socket senderSocket;
    private Socket receiverSocket;

    public FileTransferSession(Socket senderSocket, Socket receiverSocket) {
        this.senderSocket = senderSocket;
        this.receiverSocket = receiverSocket;
    }

    public Socket getSenderSocket() {
        return senderSocket;
    }

    public void setSenderSocket(Socket senderSocket) {
        this.senderSocket = senderSocket;
    }

    public Socket getReceiverSocket() {
        return receiverSocket;
    }

    public void setReceiverSocket(Socket receiverSocket) {
        this.receiverSocket = receiverSocket;
    }
}