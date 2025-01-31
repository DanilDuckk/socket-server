package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import messages.*;
import messages.broadcast.*;
import messages.bye.Bye;
import messages.bye.ByeResp;
import messages.bye.Left;
import messages.clientlist.*;
import messages.enter.*;
import messages.ping_pong.*;
import messages.privatemessage.*;
import messages.file_transfering.*;
import messages.rps.*;


import java.util.HashMap;
import java.util.Map;

public class ObjectMapper {

    private final static com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    private final static Map<Class<?>, String> objToNameMapping = new HashMap<>();

    static {
        objToNameMapping.put(Enter.class, "ENTER");
        objToNameMapping.put(EnterResp.class, "ENTER_RESP");
        objToNameMapping.put(BroadcastReq.class, "BROADCAST_REQ");
        objToNameMapping.put(BroadcastResp.class, "BROADCAST_RESP");
        objToNameMapping.put(Broadcast.class, "BROADCAST");
        objToNameMapping.put(Joined.class, "JOINED");
        objToNameMapping.put(ParseError.class, "PARSE_ERROR");
        objToNameMapping.put(Pong.class, "PONG");
        objToNameMapping.put(Hangup.class, "HANGUP");
        objToNameMapping.put(Ready.class, "READY");
        objToNameMapping.put(Ping.class, "PING");
        objToNameMapping.put(PongError.class, "PONG_ERROR");
        objToNameMapping.put(ClientList.class, "CLI_LIST");
        objToNameMapping.put(ClientListReq.class, "CLI_LIST_REQ");
        objToNameMapping.put(ClientListResp.class, "CLI_LIST_RESP");
        objToNameMapping.put(PrivMessage.class, "PRIV_MESSAGE");
        objToNameMapping.put(PrivMessageReq.class, "PRIV_MESSAGE_REQ");
        objToNameMapping.put(PrivMessageResp.class, "PRIV_MESSAGE_RESP");
        objToNameMapping.put(RpsAnswer.class, "RPS_ANSWER");
        objToNameMapping.put(RpsPlay.class, "RPS_PLAY");
        objToNameMapping.put(RpsReq.class, "RPS_REQ");
        objToNameMapping.put(RpsResp.class, "RPS_RESP");
        objToNameMapping.put(RpsResult.class, "RPS_RESULT");
        objToNameMapping.put(FileReq.class, "FILE_REQ");
        objToNameMapping.put(FileResp.class, "FILE_RESP");
        objToNameMapping.put(FileTransferStart.class, "FILE_TRANSFER_START");
        objToNameMapping.put(FileTransferReady.class, "FILE_TRANSFER_READY");
        objToNameMapping.put(FileData.class, "FILE_DATA");
        objToNameMapping.put(FileComplete.class, "FILE_COMPLETE");
        objToNameMapping.put(Bye.class, "BYE");
        objToNameMapping.put(ByeResp.class, "BYE_RESP");
        objToNameMapping.put(Left.class, "LEFT");
        objToNameMapping.put(UnknownCommand.class, "UNKNOWN_COMMAND");
    }

    public static String objectToMessage(Object object) throws JsonProcessingException {
        Class<?> clazz = object.getClass();
        String header = objToNameMapping.get(clazz);
        if (header == null) {
            throw new RuntimeException("Cannot convert this class to a message");
        }
        String body = mapper.writeValueAsString(object);
        return header + " " + body;
    }

    public static <T> T messageToObject(String message) throws JsonProcessingException {
        String[] parts = message.split(" ", 2);
        if (parts.length > 2 || parts.length == 0) {
            throw new RuntimeException("Invalid message");
        }
        String header = parts[0];
        String body = "{}";
        if (parts.length == 2) {
            body = parts[1];
        }
        Class<?> clazz = getClass(header);
        Object obj = mapper.readValue(body, clazz);
        return (T) clazz.cast(obj);
    }

    private static Class<?> getClass(String header) {
        return objToNameMapping.entrySet().stream()
                .filter(e -> e.getValue().equals(header))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find class belonging to header " + header));
    }
}
