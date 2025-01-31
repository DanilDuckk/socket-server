package server.file_transfer_server;

import java.io.*;
import java.net.Socket;

import static server.file_transfer_server.FileTransferServer.activeTransfers;
import static server.file_transfer_server.FileTransferServer.lock;

public class FileTransferHandler implements Runnable {
    private final Socket socket;

    public FileTransferHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            String role = new String(inputStream.readNBytes(1));
            String uuid = new String(inputStream.readNBytes(36));

            System.out.println("Received role: " + role + " with UUID: " + uuid);

            lock.lock();
            if(!activeTransfers.containsKey(uuid)) {
                activeTransfers.put(uuid, new FileTransferSession(null,null));
            }

            if ("S".equals(role)) {
                FileTransferSession session = FileTransferServer.activeTransfers.get(uuid);
                session.setSenderSocket(socket);
            } else if ("R".equals(role)) {
                FileTransferSession session = FileTransferServer.activeTransfers.get(uuid);
                session.setReceiverSocket(socket);
            }
            lock.unlock();
            FileTransferSession session = FileTransferServer.activeTransfers.get(uuid);
            if(session.getSenderSocket()==null || session.getReceiverSocket()==null) {
                System.out.println("Waiting for both sender and receiver to connect for UUID: " + uuid);
                return;
            }

            try {
                InputStream senderInputStream = session.getSenderSocket().getInputStream();
                OutputStream receiverOutputStream = session.getReceiverSocket().getOutputStream();
                System.out.println("Found in and out stream");
                long totalBytesTransferred = senderInputStream.transferTo(receiverOutputStream);
                System.out.println("File transfer complete for UUID: " + uuid);
                System.out.println("Total bytes transferred: " + totalBytesTransferred);

                session.getSenderSocket().close();
                session.getReceiverSocket().close();
                FileTransferServer.activeTransfers.remove(uuid);
            } catch (IOException e) {
                System.err.println("Error during file transfer for UUID " + uuid + ": " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Error in client handler: " + e.getMessage());
        }
    }
}
