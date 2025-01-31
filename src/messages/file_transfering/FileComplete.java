package messages.file_transfering;

public record FileComplete(String status, int code) {
    @Override
    public String toString() {
        if ("OK".equals(status)) {
            return "File transfer completed successfully.";
        } else {
            return "File transfer failed with error code " + code + ".";
        }
    }
}
