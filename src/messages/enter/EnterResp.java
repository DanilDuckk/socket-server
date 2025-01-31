package messages.enter;

import util.ErrorCode;

public record EnterResp(String status, int code) {
    @Override
    public String toString() {
        if ("ERROR".equals(status)) {
            // Get the error description from the ErrorCode enum
            String errorDescription = getErrorDescription(code);
            return "Failed to enter the chat. Error: " + errorDescription + ".";
        } else {
            return "Successfully entered the chat.";
        }
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