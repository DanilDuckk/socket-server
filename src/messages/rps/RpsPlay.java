package messages.rps;

public record RpsPlay(String inviter) {
    @Override
    public String toString() {
        return "You got an invitation to play RPS from "+inviter+"!";
    }
}
