package messages.ping_pong;

public record Ping() {
    @Override
    public String toString() {
        return "PING";
    }
}