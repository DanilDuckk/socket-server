package messages.privatemessage;

public record PrivMessage(String message) {
    @Override
    public String toString() {
        return "Private message: \"" + message + "\"";
    }
}
