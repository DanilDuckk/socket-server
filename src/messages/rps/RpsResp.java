package messages.rps;

import util.ErrorCode;

public record RpsResp(String status, int code) {
    @Override
    public String toString() {
        if ("SUCCESS".equals(status)) {
            return "Invitation sent successfully.";
        } else {
            ErrorCode errorCode = getErrorCodeByCode(code);
            return "Invitation failed: " + errorCode.getDescription();
        }
    }

    private ErrorCode getErrorCodeByCode(int code) {
        for (ErrorCode error : ErrorCode.values()) {
            if (error.getCode() == code) {
                return error;
            }
        }
        return null;
    }
}