package messages.rps;

public record RpsAnswer(String choice) {
    @Override
    public String toString() {
        return "Client chose: " + choice;
    }
}