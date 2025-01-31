package messages.privatemessage;

public record PrivMessageReq(String user, String message) {
    @Override
    public String toString() {
        return "Request to send a private message to " + user + ": \"" + message + "\"";
    }
}