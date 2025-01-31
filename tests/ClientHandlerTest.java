import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import server.ClientHandler;

import java.io.*;
import java.net.*;

class ClientHandlerTest {

    private ClientHandler clientHandler;
    private Socket mockSocket;
    private InputStream mockInputStream;
    private OutputStream mockOutputStream;
    private BufferedReader mockReader;
    private PrintWriter mockWriter;

    @BeforeEach
    void setUp() throws IOException {
        mockSocket = mock(Socket.class);
        mockInputStream = mock(InputStream.class);
        mockOutputStream = mock(OutputStream.class);
        mockReader = mock(BufferedReader.class);
        mockWriter = mock(PrintWriter.class);

        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        when(mockSocket.getInputStream()).thenReturn(mockInputStream);

        clientHandler = new ClientHandler(mockSocket);
        clientHandler.run();
    }

    @Test
    void testHandleLogin_Valid() throws Exception {
        String command = "ENTER testUser";
        clientHandler.handleCommand(command);
        verify(mockWriter).println(contains("SUCCESS"));
    }

    @Test
    void testBroadcastMessage() throws Exception {
        String command = "BROADCAST_REQ Hello!";
        clientHandler.handleCommand(command);
        verify(mockWriter).println(contains("SUCCESS"));
    }

    @Test
    void testPrivateMessage() throws Exception {
        String command = "PRIV_MESSAGE_REQ receiver Hello!";
        clientHandler.handleCommand(command);
        verify(mockWriter).println(contains("SUCCESS"));
    }

    @Test
    void testInvalidCommand() throws Exception {
        String command = "INVALID_COMMAND";
        clientHandler.handleCommand(command);
        verify(mockWriter).println(contains("UnknownCommand"));
    }
}
