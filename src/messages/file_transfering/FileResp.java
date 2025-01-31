package messages.file_transfering;

public record FileResp(String fileReceiver, String fileSender, String file, String status, String checksum, int code) {
    @Override
    public String toString() {
        if ("ERROR".equals(status)) {
            return "File transfer response failed with error code " + code + ".";
        } else {
            return "File transfer accepted with checksum: " + checksum + ".";
        }
    }
}