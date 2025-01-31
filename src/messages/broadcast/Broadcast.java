package messages.broadcast;

public record Broadcast(String username, String message) {

    @Override
    public String toString() {
        return username + " broadcasts: \"" + message + "\"";
    }
}
