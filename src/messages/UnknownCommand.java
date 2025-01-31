package messages;

public record UnknownCommand() {
    @Override
    public String toString() {
        return "Server didn't recognise the response you sent";
    }
}
