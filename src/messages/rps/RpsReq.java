package messages.rps;

public record RpsReq(String inviter, String invitee, String choice) {
    @Override
    public String toString() {
        return "Rock-Paper-Scissors Invitation Request: " + inviter + " invites " + invitee + " to play with choice: " + choice;
    }
}
