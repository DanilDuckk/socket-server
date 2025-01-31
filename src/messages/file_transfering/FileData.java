package messages.file_transfering;

public record FileData(byte[] data, String checkSome) {
    @Override
    public String toString() {
        return "File data of size " + data.length + " bytes.";
    }
}
