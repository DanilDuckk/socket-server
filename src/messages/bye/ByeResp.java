package messages.bye;

public record ByeResp(String status) {
    @Override
    public String toString() {
        return "Leave status: " + status;
    }
}
