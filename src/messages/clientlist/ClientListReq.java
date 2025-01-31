package messages.clientlist;

public record ClientListReq() {
    @Override
    public String toString() {
        return "Request to retrieve the list of connected clients.";
    }
}
