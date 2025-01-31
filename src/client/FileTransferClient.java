package client;

import java.io.*;
import java.net.Socket;

public class FileTransferClient {
    private static final String SAVE_DIRECTORY = "src/client/received_files/";
    private static final String SEND_DIRECTORY = "src/client/files_to_send/";
    private static final int FILE_TRANSFER_PORT = 1338;

    public static void startFileTransferringThread(String uuid, String fileName) {
        new Thread(() -> {
            File fileToSend = new File(SEND_DIRECTORY + fileName);
            if (!fileToSend.exists() || !fileToSend.isFile()) {
                System.err.println("File not found: " + fileName);
                return;
            }

            try {
                Socket fileSocket = new Socket("localhost", FILE_TRANSFER_PORT);
                OutputStream outputStream = fileSocket.getOutputStream();
                InputStream fileInputStream = new FileInputStream(fileToSend);

                outputStream.write('S');
                outputStream.write(uuid.getBytes());
                fileInputStream.transferTo(outputStream);
                outputStream.close();
                fileSocket.close();
                System.out.println("File transfer completed: " + fileName);

                for (int i = 0; i < FileManager.myRequests.size(); i++) {
                    if(FileManager.myRequests.get(i).filename().equals(fileName)) {
                        FileManager.myRequests.remove(i);
                        return;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error transferring file: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static void startFileReceiverThread(String uuid, String fileName) {
        new Thread(() -> {
            File saveDir = new File(SAVE_DIRECTORY);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            try {
                Socket fileSocket = new Socket("localhost", FILE_TRANSFER_PORT);
                InputStream inputStream = fileSocket.getInputStream();
                OutputStream fileOutputStream = new FileOutputStream(SAVE_DIRECTORY + "/" + fileName);
                OutputStream outputStream = fileSocket.getOutputStream();

                outputStream.write('R');
                outputStream.write(uuid.getBytes());
                inputStream.transferTo(fileOutputStream);
                fileSocket.close();
                System.out.println("File received successfully.");

                for (int i = 0; i < FileManager.senders.size(); i++) {
                    if(FileManager.senders.get(i).filename().equals(fileName)) {
                        FileManager.senders.remove(i);
                        return;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error receiving file: " + e.getMessage());
            }
        }).start();
    }
}
