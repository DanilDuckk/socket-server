package messages.clientlist;

import util.ErrorCode;

public record ClientListResp(String status, int code) {
    @Override
    public String toString() {
        if ("ERROR".equals(status)) {
            String errorDescription = getErrorDescription(code);
            return "Failed to retrieve the client list. Error: " + errorDescription + ".";
        } else {
            return "Client list retrieval succeeded.";
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