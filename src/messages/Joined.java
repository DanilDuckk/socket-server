package messages;

public record Joined(String username) {
    @Override
    public String toString() {
        return "New user joined with name: " + username;
    }
}
