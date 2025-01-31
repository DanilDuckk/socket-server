package messages;

public record Ready(String version) {
    @Override
    public String toString() {
        return "The server with version " + version + " is running";
    }
}
