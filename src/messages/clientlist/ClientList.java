package messages.clientlist;

import java.util.List;

public record ClientList(List<String> list) {
    @Override
    public String toString() {
        if (list.isEmpty()) {
            return "No clients are currently connected.";
        } else {
            return "Connected clients: " + String.join(", ", list);
        }
    }
}
