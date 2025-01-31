package messages.file_transfering;

public record FileTransferReady(String role, String uuid, String file) {
    @Override
    public String toString() {
        return "File transfer ready with uuid: " + uuid + ".";
    }
}