package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import messages.file_transfering.FileReq;
import messages.file_transfering.FileResp;
import util.ObjectMapper;

import java.io.PrintWriter;

public class FileTransferringHelper {
    public static void requestTransferring(String command, String username) throws JsonProcessingException {
        FileReq fileReq = ObjectMapper.messageToObject(command);
        String receiver = fileReq.to();
        String filename = fileReq.filename();
        double size = fileReq.size();
        PrintWriter userWriter = ClientsDatabase.getUserWriter(receiver);
        userWriter.println(ObjectMapper.objectToMessage(new FileReq(username,filename,size)));
        userWriter.flush();
    }

    public static void responseTransferring(String command) throws JsonProcessingException {
        FileResp fileResp = ObjectMapper.messageToObject(command);
        String sender = fileResp.fileSender();
        PrintWriter userWriter = ClientsDatabase.getUserWriter(sender);
        userWriter.println(ObjectMapper.objectToMessage(fileResp));
        userWriter.flush();
    }
}
