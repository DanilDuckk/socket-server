package server;

public class RPSManager {
    private boolean isGameRunning = false;
    private String inviter = "";
    private String invitee = "";
    private String inviterChoice = "";
    private String inviteeChoice = "";

    public RPSManager() {}

    public RPSManager(String inviter, String invitee) {
        this.inviter = inviter;
        this.invitee = invitee;
    }

    public String showWinner() {
        if (inviterChoice == null || inviteeChoice == null) {
            throw new IllegalStateException("Both players must make a choice before playing.");
        }

        String result;
        if (inviterChoice.equals(inviteeChoice)) {
            result = "It's a tie!";
        } else if (winsAgainst(inviterChoice, inviteeChoice)) {
            result = "inviter";
        } else {
            result = "invitee";
        }

        return result;
    }

    private boolean winsAgainst(String choice1, String choice2) {
        return (choice1.equals("rock") && choice2.equals("scissors"))
                || (choice1.equals("scissors") && choice2.equals("paper"))
                || (choice1.equals("paper") && choice2.equals("rock"));
    }

    public void reset(){
        inviter = "";
        invitee = "";
        inviterChoice = "";
        inviteeChoice = "";
        isGameRunning = false;
    }

    public void setInviterChoice(String choice){
        inviterChoice = choice;
    }

    public void setGameRunningTrue(){
        isGameRunning = true;
    }

    public void setInviteeChoice(String choice){
        inviteeChoice = choice;
    }

    public boolean isGameRunning(){
        return isGameRunning;
    }

    public String getInviter() {
        return inviter;
    }

    public String getInvitee() {
        return invitee;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public void setInvitee(String invitee) {
        this.invitee = invitee;
    }
}
