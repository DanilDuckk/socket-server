package messages.ping_pong;

import util.ErrorCode;

public record PongError(int code) {
    @Override
    public String toString() {
        String errorDescription = getErrorDescription(code);
        return "Pong error occurred. Reason: " + errorDescription + ".";
    }

    private String getErrorDescription(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode.getDescription();
            }
        }
        return "Unknown error";
    }
}