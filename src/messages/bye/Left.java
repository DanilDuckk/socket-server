package messages.bye;

public record Left(String username) {
    @Override
    public String toString() {
        return username+" has left! Say goodbye to him/her!";
    }
}
