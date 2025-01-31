package util;

public enum ErrorCode {
    USER_ALREADY_EXISTS(1000, "User with this name already exists"),
    INVALID_USERNAME(1001, "Username has an invalid format or length"),
    ALREADY_LOGGED_IN(1002, "Already logged in"),
    USER_NOT_LOGGED_IN_BROADCAST(2000, "User is not logged in"),
    PONG_WITHOUT_PING(3000, "Pong without ping"),
    NO_PONG_RECEIVED(3001, "No pong received"),
    USER_NOT_LOGGED_IN_LIST(4000, "User is not logged in"),
    USER_NOT_FOUND_PRIV(5000, "No such user found"),
    USER_NOT_LOGGED_IN_PRIV(5001, "User is not logged in"),
    USER_NOT_FOUND_RPS(6000, "User not found"),
    GAME_IS_RUNNING(6001, "The game is already running"),
    NOT_PARTICIPATED_IN_GAME(6002, "You're not participation in the game"),;

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}