import static org.mockito.Mockito.*;

import client.ProduceMessage;
import messages.file_transfering.FileResp;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

class ProduceMessageTest {

    private PrintWriter mockWriter;

    @BeforeEach
    void setUp() {
        mockWriter = mock(PrintWriter.class);
        ProduceMessage.writer = mockWriter;
    }

    @Test
    void testLogin() {
        String username = "testUser";
        ProduceMessage.login(username);
        verify(mockWriter).println(contains("Enter"));
    }

    @Test
    void testBroadcastMessage() {
        String message = "Hello World";
        ProduceMessage.broadcastMessage(message);
        verify(mockWriter).println(contains(message));
    }

    @Test
    void testSendPrivateMessage() {
        String username = "receiver";
        String message = "Private message";
        ProduceMessage.sendPrivateMessage(username, message);
        verify(mockWriter).println(contains(username));
        verify(mockWriter).println(contains(message));
    }

    @Test
    void testFileTransferringManager() {
        ProduceMessage.fileTransferringManager();
        verify(mockWriter, times(1)).println(anyString());
    }

    @Test
    void testPrepareFileTransfer() {
        FileResp fileResp = mock(FileResp.class);
        ProduceMessage.prepareFileTransfer(fileResp);
        verify(mockWriter).println(anyString());
    }

    @Test
    void testDisconnect() {
        ProduceMessage.disconnect();
        verify(mockWriter).println(contains("Bye"));
    }
}
