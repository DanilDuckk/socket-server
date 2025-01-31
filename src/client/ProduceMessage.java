package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import messages.broadcast.BroadcastReq;
import messages.bye.Bye;
import messages.clientlist.ClientListReq;
import messages.enter.Enter;
import messages.file_transfering.FileReq;
import messages.file_transfering.FileResp;
import messages.file_transfering.FileTransferStart;
import messages.ping_pong.Pong;
import messages.privatemessage.PrivMessageReq;
import messages.rps.RpsAnswer;
import messages.rps.RpsReq;
import util.Input;
import util.ObjectMapper;

import java.io.*;
import java.util.List;

public class ProduceMessage {
    public static PrintWriter writer;

    private static void sendMessage(Object messageObject) {
        try {
            String result = ObjectMapper.objectToMessage(messageObject);
            writer.println(result);
            writer.flush();
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing message: " + e.getMessage());
            throw new RuntimeException("Failed to process message", e);
        }
    }

    public static void login(String username) {
        Enter enter = new Enter(username);
        sendMessage(enter);
    }

    public static void broadcastMessage(String message) {
        BroadcastReq broadcastReq = new BroadcastReq(message);
        sendMessage(broadcastReq);
    }

    public static void sendPrivateMessage(String username, String message) {
        PrivMessageReq privMessageReq = new PrivMessageReq(username, message);
        sendMessage(privMessageReq);
    }

    public static void requestListOfUsers() {
        ClientListReq clientListReq = new ClientListReq();
        sendMessage(clientListReq);
    }

    public static void pong() {
        Pong pong = new Pong();
        sendMessage(pong);
    }

    public static void rockPaperScissorsGameManager() {
        System.out.println("/invite");
        System.out.println("/choose");
        System.out.print("Choice: ");
        String choice = Input.readString();
        switch (choice){
            case "/invite" -> {
                System.out.println("Enter user's name you want to invite:");
                String invitee = Input.readString();
                System.out.println("Enter your choice (rock,paper,scissors):");
                String gameElement = correctRPSChoice();
                RpsReq rpsReq = new RpsReq(Client.username,invitee,gameElement);
                sendMessage(rpsReq);
            }
            case "/choose" -> {
                System.out.println("Enter your choice (rock,paper,scissors):");
                String gameElement = correctRPSChoice();
                RpsAnswer rpsAnswer = new RpsAnswer(gameElement);
                sendMessage(rpsAnswer);
            }
        }
    }

    public static void fileTransferringManager() {
        ProduceMessage.printRequests(FileManager.senders,"OTHER REQUESTS");
        ProduceMessage.printRequests(FileManager.myRequests,"MY REQUESTS");

        System.out.println("1. Send a file");
        System.out.println("2. Accept a file");
        System.out.println("3. Reject a file");
        System.out.print("Choice: ");
        int choice = Input.readInt();
        switch (choice){
            case 1 -> sendMessage(FileManager.sendRequest());
            case 2 -> sendMessage(FileManager.acceptRequest());
            case 3 -> sendMessage(FileManager.rejectRequest());
            default -> System.out.println("Invalid choice");
        }
    }

    public static void prepareFileTransfer(FileResp fileResp){
        String receiver = fileResp.fileReceiver();
        String sender = Client.username;
        String fileName = fileResp.file();
        FileTransferStart fileTransferStart = new FileTransferStart(sender,receiver,fileName);
        sendMessage(fileTransferStart);
    }

    public static void disconnect(){
        Bye bye = new Bye();
        sendMessage(bye);
    }

    private static void printRequests(List<FileReq> list, String listName){
        System.out.println("-------"+listName+"--------");
        if(list.isEmpty()){
            System.out.println("My requests list is empty");
        } else {
            for (FileReq requests : list) {
                System.out.println(requests);
            }
        }
        System.out.println("--------------------------");
    }

    private static String correctRPSChoice(){
        String gameElement = Input.readString();
        while (!gameElement.equalsIgnoreCase("rock") && !gameElement.equalsIgnoreCase("paper") && !gameElement.equalsIgnoreCase("scissors")){
            System.out.println("Wrong input, try again:");
            gameElement = Input.readString();
        }
        return gameElement;
    }
}
