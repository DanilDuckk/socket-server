package client;

import messages.file_transfering.FileReq;
import messages.file_transfering.FileResp;
import messages.file_transfering.FileTransferReady;
import util.Input;
import util.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 1337;
    private boolean autoPong = true;

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private PrintWriter writer;
    public static String username;

    public static void main(String[] args) {
        new Client().run();
    }

    public void run() {
        setupConnection();
        setupInput();
        setupOutput();
    }

    public void setupConnection(){
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println("Server is not running, can't establish connection with ADDRESS: "+SERVER_ADDRESS+" and PORT: "+SERVER_PORT);
            System.exit(1);
        }
    }

    public void setupOutput(){
        new Thread(() -> {
            writer = new PrintWriter(outputStream);
            ProduceMessage.writer = this.writer;

            while (true) {
                printMenu();
                String choice = Input.readString();

                switch (choice) {
                    case "/enter" -> {
                        System.out.println("----------LOGIN-----------");
                        System.out.println("Enter an username: ");
                        String username = Input.readString();
                        this.username = username;
                        ProduceMessage.login(username);
                        System.out.println("--------LOGIN END---------");
                    }
                    case "/broadcast" -> {
                        System.out.println("Enter a message: ");
                        String message = Input.readString();
                        ProduceMessage.broadcastMessage(message);
                    }
                    case "/private message" -> {
                        System.out.println("Enter a username: ");
                        String username = Input.readString();
                        System.out.println("Enter a message: ");
                        String message = Input.readString();
                        ProduceMessage.sendPrivateMessage(username,message);
                    }
                    case "/users list" -> {
                        ProduceMessage.requestListOfUsers();
                    }
                    case "/rps manager" -> {
                        ProduceMessage.rockPaperScissorsGameManager();
                    }
                    case "/file transfer manager" -> {
                        ProduceMessage.fileTransferringManager();
                    }
                    case "/pong" -> {
                        ProduceMessage.pong();
                    }
                    case "/disconnect" -> {
                        ProduceMessage.disconnect();
                        System.exit(1 );
                    }
                    default -> System.out.println("Invalid choice");
                }

                writer.flush();
            }
        }).start();
    }

    public void setupInput(){
        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                try {
                    String line = reader.readLine();
                    System.out.println(ObjectMapper.messageToObject(line).toString());
                    handleIncomingMessages(line);
                } catch (IOException e) {
                    System.out.println();
                    System.err.println("The server aborted the connection.");
                }
            }
        }).start();
    }

    public void printMenu(){
        System.out.println("-----------MENU-----------");
        System.out.println("/enter");
        System.out.println("/broadcast");
        System.out.println("/users list");
        System.out.println("/private message");
        System.out.println("/rps manager");
        System.out.println("/file transfer manager");
        System.out.println("/pong");
        System.out.println("/disconnect");
        System.out.println("--------------------------");
        System.out.print("Choice: ");
    }

    private void handleIncomingMessages(String line) throws IOException {
        if(autoPong){
            if (line.equalsIgnoreCase("PING")) {
                System.out.println("PONG");
                writer.println("PONG");
                writer.flush();
            }
        }
        if(line.contains("FILE_REQ")){
            FileReq fileReq = ObjectMapper.messageToObject(line);
            FileManager.senders.add(fileReq);
        }
        if(line.contains("FILE_RESP") && line.contains("ERROR")){
            FileResp fileReq = ObjectMapper.messageToObject(line);
            for (FileReq fr : FileManager.myRequests) {
                if(fr.filename().equals(fileReq.file())){
                    FileManager.myRequests.remove(fr);
                    return;
                }
            }
        }
        if(line.startsWith("FILE_RESP") && line.contains("ACCEPT")){
            FileResp fileResp = ObjectMapper.messageToObject(line);
            ProduceMessage.prepareFileTransfer(fileResp);
        }
        if(line.startsWith("FILE_TRANSFER_READY")){
            FileTransferReady fileTransferReady = ObjectMapper.messageToObject(line);
            String uuid = fileTransferReady.uuid();
            String fileName = fileTransferReady.file();
            if(fileTransferReady.role().equalsIgnoreCase("receiver")){
                FileTransferClient.startFileReceiverThread(uuid, fileName);
            } else if (fileTransferReady.role().equalsIgnoreCase("sender")){
                FileTransferClient.startFileTransferringThread(uuid, fileName);
            }
        }
        if(line.startsWith("BYE_RESP") && line.contains("SUCCESS")){
            writer.close();
            socket.close();
            System.exit(1);
        }
        if (line.startsWith("HANGUP")) {
            writer.close();
            socket.close();
            System.exit(0);
        }
    }
}
