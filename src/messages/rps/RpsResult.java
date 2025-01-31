package messages.rps;

public record RpsResult(String result) {
    @Override
    public String toString() {
        if ("won".equals(result)) {
            return "You won the game!";
        } else if ("lost".equals(result)) {
            return "You lost the game.";
        } else {
            return "It's a tie!";
        }
    }
}