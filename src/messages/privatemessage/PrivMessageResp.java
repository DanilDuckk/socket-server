package messages.privatemessage;

import util.ErrorCode;

public record PrivMessageResp(String status, int error) {
    @Override
    public String toString() {
        if ("SUCCESS".equals(status)) {
            return "Private message sent successfully.";
        } else {
            String errorDescription = getErrorDescription(error);
            return "Failed to send private message. Error: " + errorDescription + ".";
        }
    }

    private String getErrorDescription(int error) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == error) {
                return errorCode.getDescription();
            }
        }
        return "Unknown error";
    }
}
