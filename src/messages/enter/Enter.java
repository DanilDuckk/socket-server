package messages.enter;

public record Enter(String username) {
    @Override
    public String toString(){
        return username + " has entered the chat.";
    }
}
