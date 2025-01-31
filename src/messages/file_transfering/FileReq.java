package messages.file_transfering;

public record FileReq(String to, String filename, double size) {
    @Override
    public String toString() {
        return "File transfer request from " + to + " for file '" + filename + "' (" + size + " bytes).";
    }
}
