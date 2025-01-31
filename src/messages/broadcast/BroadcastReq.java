package messages.broadcast;

public record BroadcastReq(String message) {
    @Override
    public String toString() {
        return "Broadcast request for this message: " + message;
    }
}