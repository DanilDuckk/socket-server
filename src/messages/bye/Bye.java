package messages.bye;

public record Bye() {
    @Override
    public String toString() {
        return "You are leaving";
    }
}
