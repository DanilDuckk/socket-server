package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import messages.Joined;
import messages.Ready;
import messages.UnknownCommand;
import messages.broadcast.Broadcast;
import messages.broadcast.BroadcastReq;
import messages.broadcast.BroadcastResp;
import messages.bye.ByeResp;
import messages.bye.Left;
import messages.clientlist.ClientList;
import messages.clientlist.ClientListResp;
import messages.enter.Enter;
import messages.enter.EnterResp;
import messages.file_transfering.FileTransferReady;
import messages.file_transfering.FileTransferStart;
import messages.privatemessage.PrivMessage;
import messages.privatemessage.PrivMessageReq;
import messages.privatemessage.PrivMessageResp;
import messages.rps.*;
import util.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.UUID;


public class ClientHandler implements Runnable {
    private final Socket socket;
    private String username = "";
    private PrintWriter writer;
    private PingManager pingManager;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Client connected");
        try (
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            writer = new PrintWriter(outputStream, true);

            try {
                writer.println(ObjectMapper.objectToMessage(new Ready("1.1")));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            pingManager = new PingManager(writer, socket, username);

            while (true) {
                String command = reader.readLine();
                if (command == null) {
                    break;
                }
                handleCommand(command);
            }
        } catch (IOException e) {
            if(username.isEmpty()){
                System.err.println("Socket closed for unknown client");
            } else System.err.println("Socket closed for this user `"+username);
        } finally {
            pingManager.stopPingTimer();
            pingManager.disconnectClient();

            if (!username.isEmpty()) {
                ClientsDatabase.removerUser(username);
            }
        }
    }

    public void handleCommand(String command) throws IOException {
        System.out.println(command);
        if (command.startsWith("ENTER")) {
            writer.println(ObjectMapper.objectToMessage(handleLogin(command)));
        } else if (command.startsWith("BROADCAST_REQ")) {
            writer.println(ObjectMapper.objectToMessage(broadcastMessage(command)));
        } else if (command.startsWith("CLI_LIST_REQ")) {
            if (username.isEmpty()) {
                writer.println(ObjectMapper.objectToMessage(new ClientListResp("ERROR", 4000)));
            } else writer.println(ObjectMapper.objectToMessage(listUsers()));
        } else if (command.startsWith("PRIV_MESSAGE_REQ")) {
            writer.println(ObjectMapper.objectToMessage(sendPrivateMessage(command)));
        } else if (command.startsWith("RPS_REQ")) {
            writer.println(ObjectMapper.objectToMessage(startRPSGame(command)));
        } else if (command.startsWith("RPS_ANSWER")) {
            showResultsOfRPSGame(command);
        } else if (command.contains("FILE")) {
            prepareFileTransferring(command);
        } else if (command.startsWith("BYE")) {
            disconnect();
            return;
        } else if (command.equals("PONG")){
            if (!pingManager.isAwaitingPong()) {
                pingManager.sendPongError(3001);
            } else {
                pingManager.setIsAwaitingPongFalse();
            }
        } else {
            writer.println(ObjectMapper.objectToMessage(new UnknownCommand()));
        }
        writer.flush();
    }

    private EnterResp handleLogin(String command) throws JsonProcessingException {
        Enter enterMessage = ObjectMapper.messageToObject(command);
        String username = enterMessage.username();
        if (username.equalsIgnoreCase(this.username)){
            return new EnterResp("ERROR", 1002);
        } else if(ClientsDatabase.userWithTheSameName(username)){
            return new EnterResp("ERROR", 1000);
        } else if (username.length()<2){
            return new EnterResp("ERROR", 1001);
        }
        this.username = username;
        System.out.println(username+" logged in");
        ClientsDatabase.addClient(username,this);
        List<PrintWriter> writers = ClientsDatabase.getOthersWriterList(username);
        for (PrintWriter writer : writers){
            writer.println(ObjectMapper.objectToMessage(new Joined(username)));
            writer.flush();
        }
        pingManager.startPingTimer();
        return new EnterResp("SUCCESS",0);
    }

    private BroadcastResp broadcastMessage(String command) throws JsonProcessingException {
        if(username.isEmpty()){
            return new BroadcastResp("ERROR", 2000);
        }
        BroadcastReq broadcastReq = ObjectMapper.messageToObject(command);
        String messageToBroadcast = broadcastReq.message();
        System.out.println("User "+username+" says: "+messageToBroadcast);
        List<PrintWriter> writers = ClientsDatabase.getOthersWriterList(username);
        for (PrintWriter writer : writers){
            writer.println(new Broadcast(username,messageToBroadcast));
            writer.flush();
        }
        return new BroadcastResp("SUCCESS",0);
    }

    private ClientList listUsers() {
        List<String> names = ClientsDatabase.getClientNames(username);
        return new ClientList(names);
    }

    private PrivMessageResp sendPrivateMessage(String command) throws JsonProcessingException {
        if (username.isEmpty()) {
            return new PrivMessageResp("ERROR", 5001);
        }
        PrivMessageReq privMessageReq = ObjectMapper.messageToObject(command);
        String messageToSend = privMessageReq.message();
        String receiver = privMessageReq.user();
        PrintWriter userWriter = ClientsDatabase.getUserWriter(receiver);
        if(userWriter == null){
            return new PrivMessageResp("ERROR", 5000);
        }
        userWriter.println(new PrivMessage(messageToSend));
        userWriter.flush();
        return new PrivMessageResp("SUCCESS",0);
    }

    private RpsResp startRPSGame(String command) throws JsonProcessingException {
        RpsReq rpsReq = ObjectMapper.messageToObject(command);
        PrintWriter userWriter = ClientsDatabase.getUserWriter(rpsReq.invitee());
        if(userWriter == null){
            return new RpsResp("ERROR", 6000);
        }
        if(Server.rpsManager.isGameRunning()){
            return new RpsResp("ERROR",6001);
        }
        Server.rpsManager.setInviter(rpsReq.inviter());
        Server.rpsManager.setInvitee(rpsReq.invitee());
        Server.rpsManager.setGameRunningTrue();
        Server.rpsManager.setInviterChoice(rpsReq.choice());

        userWriter.println(ObjectMapper.objectToMessage(new RpsPlay(rpsReq.inviter())));
        userWriter.flush();
        return new RpsResp("SUCCESS",0);
    }

    private void showResultsOfRPSGame(String command) throws JsonProcessingException {
        RpsAnswer rpsAnswer = ObjectMapper.messageToObject(command);
        if(!Server.rpsManager.getInvitee().equalsIgnoreCase(username)){
            if(Server.rpsManager.isGameRunning()){
                RpsResp rpsResp = new RpsResp("ERROR",6001);
                writer.println(rpsResp);
                return;
            }
            RpsResp rpsResp = new RpsResp("ERROR",6002);
            writer.println(rpsResp);
            return;
        }

        Server.rpsManager.setInviteeChoice(rpsAnswer.choice());
        String winner = Server.rpsManager.showWinner();

        PrintWriter inviterWriter = ClientsDatabase.getUserWriter(Server.rpsManager.getInviter());
        PrintWriter inviteeWriter = ClientsDatabase.getUserWriter(Server.rpsManager.getInvitee());

        if(winner.equals("It's a tie!")){
            inviterWriter.println(new RpsResult("tie"));
            inviteeWriter.println(new RpsResult("tie"));
            inviterWriter.flush();
            inviteeWriter.flush();
        } else if (winner.equals("inviter")){
            inviterWriter.println(ObjectMapper.objectToMessage(new RpsResult("won")));
            inviteeWriter.println(ObjectMapper.objectToMessage(new RpsResult("lost")));
            inviterWriter.flush();
            inviteeWriter.flush();
        } else if (winner.equals("invitee")){
            inviterWriter.println(ObjectMapper.objectToMessage(new RpsResult("lost")));
            inviteeWriter.println(ObjectMapper.objectToMessage(new RpsResult("won")));
            inviterWriter.flush();
            inviteeWriter.flush();
        }
        Server.rpsManager.reset();
    }

    private void prepareFileTransferring(String command) throws IOException {
        if (command.startsWith("FILE_REQ")) {
            FileTransferringHelper.requestTransferring(command,username);
        } else if (command.startsWith("FILE_RESP")) {
            FileTransferringHelper.responseTransferring(command);
        } else if(command.startsWith("FILE_TRANSFER_START")) {
            FileTransferStart transferStart = ObjectMapper.messageToObject(command);
            String uuid = UUID.randomUUID().toString();
            notifyClients(transferStart,uuid);
        }
    }

    private static void notifyClients(FileTransferStart transferStart, String uuid) {
        PrintWriter senderHandler = ClientsDatabase.getUserWriter(transferStart.sender());
        PrintWriter receiverHandler = ClientsDatabase.getUserWriter(transferStart.receiver());

        FileTransferReady senderReady = new FileTransferReady("sender", uuid, transferStart.filename());
        FileTransferReady receiverReady = new FileTransferReady("receiver", uuid, transferStart.filename());

        if (senderHandler != null) {
            try {
                senderHandler.println(ObjectMapper.objectToMessage(senderReady));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            senderHandler.flush();
        }

        if (receiverHandler != null) {
            try {
                receiverHandler.println(ObjectMapper.objectToMessage(receiverReady));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            receiverHandler.flush();
        }
    }

    private void disconnect(){
        try {
            if (!socket.isClosed()) {
                writer.println(ObjectMapper.objectToMessage(new ByeResp("SUCCESS")));
                writer.flush();
            }

            if (!username.isEmpty()) {
                List<PrintWriter> writers = ClientsDatabase.getOthersWriterList(username);
                for (PrintWriter userWriter : writers) {
                    userWriter.println(ObjectMapper.objectToMessage(new Left(username)));
                    userWriter.flush();
                }
                writer.close();
                socket.close();
                ClientsDatabase.removerUser(username);
            }
        } catch (IOException e) {
            System.err.println("Error processing JSON during disconnection for user `" + username + "`: " + e.getMessage());
        }
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public Socket getSocket(){
        return socket;
    }
}