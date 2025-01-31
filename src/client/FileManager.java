package client;

import messages.file_transfering.FileReq;
import messages.file_transfering.FileResp;
import util.FolderScanner;
import util.Input;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static List<FileReq> senders = new ArrayList<>();
    public static List<FileReq> myRequests = new ArrayList<>();

    public static FileReq sendRequest() {
        System.out.print("Type the receiver name: ");
        String receiverName = Input.readString();

        System.out.println("----------FILES-----------");
        List<Path> files = FolderScanner.getFiles("src/client/files_to_send");
        files.forEach(file -> System.out.println(file.getFileName()));
        System.out.println("--------------------------");
        System.out.print("Type a file name you want to send: ");
        String fileName = Input.readString();

        for (Path file : files) {
            if(fileName.equals(file.getFileName().toString())) {
                try {
                    long fileSize = Files.size(file);
                    FileReq fileReq = new FileReq(receiverName, fileName, fileSize);
                    myRequests.add(fileReq);
                    return new FileReq(receiverName, fileName, fileSize);
                } catch (IOException e) {
                    System.err.println("Error retrieving file size: " + e.getMessage());
                }
            }
        }
        return null;
    }

    public static FileResp acceptRequest() {
        System.out.println("Enter a username from whom you want to accept a request:");
        String senderName = Input.readString();
        String fileName = findSendersFileName(senderName);
        return new FileResp(Client.username, senderName, fileName, "ACCEPT", "MD5", 0);
    }

    public static FileResp rejectRequest() {
        System.out.println("Enter a username from whom you want to reject a request:");
        String senderName = Input.readString();
        String fileName = findSendersFileName(senderName);
        if(!senders.contains(senderName)) {
            FileResp fileResp = new FileResp(senderName, Client.username, fileName, "ERROR", "MD5", 7001);
            senders.removeIf(toDelete -> toDelete.to().equalsIgnoreCase(senderName));
            return fileResp;
        }
        System.out.println("No such sender found");
        return null;
    }

    public static String findSendersFileName(String senderName) {
        String fileName = "";
        for (FileReq fileReq : senders) {
            if (fileReq.to().equalsIgnoreCase(senderName)){
                fileName = fileReq.filename();
                return fileName;
            }
        }
        return null;
    }

}
