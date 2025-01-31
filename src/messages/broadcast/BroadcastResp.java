package messages.broadcast;

import util.ErrorCode;

public record BroadcastResp(String status, int code) {
    @Override
    public String toString() {
        if ("ERROR".equals(status)) {
            String errorDescription = getErrorDescription(code);
            return "Broadcast failed with error: " + errorDescription + ".";
        } else {
            return "Broadcast succeeded.";
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