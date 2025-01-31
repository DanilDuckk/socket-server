package messages.file_transfering;

public record FileTransferStart(String sender, String receiver, String filename) {
    @Override
    public String toString() {
        return "File transfer started to " + receiver + " for file '" + filename + "' from "+sender+".";
    }
}
