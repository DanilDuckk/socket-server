package server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientsDatabase {
    private static final Map<String, ClientHandler> clients = new HashMap<>();

    public static void addClient(String username, ClientHandler clientHandler){
        clients.put(username, clientHandler);
    }

    public static List<String> getClientNames(String username){
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            String key = entry.getKey();
            if(key.equals(username)){
                names.add(key+" <- YOU");
            } else names.add(key);
        }
        return names;
    }

    public static List<PrintWriter> getOthersWriterList(String username) {
        List<PrintWriter> writers = new ArrayList<>();
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            String key = entry.getKey();
            ClientHandler clientHandler = entry.getValue();

            if (!key.equals(username)) {
                PrintWriter writer = clientHandler.getWriter();
                writers.add(writer);
            }
        }
        return writers;
    }

    public static PrintWriter getUserWriter(String username) {
        PrintWriter writer;
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            String key = entry.getKey();
            ClientHandler clientHandler = entry.getValue();

            if (key.equalsIgnoreCase(username)) {
                writer = clientHandler.getWriter();
                return writer;
            }
        }
        return null;
    }

    public static boolean userWithTheSameName(String username) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public static void removerUser(String username) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase(username)) {
                clients.remove(key);
                break;
            }
        }
    }
}
