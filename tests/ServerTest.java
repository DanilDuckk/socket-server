import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import server.ClientHandler;
import server.Server;

import java.io.*;
import java.net.*;

class ServerTest {

    private Server server;
    private ServerSocket mockServerSocket;

    @BeforeEach
    void setUp() throws IOException {
        mockServerSocket = mock(ServerSocket.class);
        when(mockServerSocket.accept()).thenReturn(mock(Socket.class));
        server = new Server();
        server.startServer();
    }

    @Test
    void testEstablishConnection() throws IOException {
        server.establishConnection();
        verify(mockServerSocket).accept();
    }

    @Test
    void testHandleCommand() throws Exception {
        Socket mockSocket = mock(Socket.class);
        ClientHandler clientHandler = new ClientHandler(mockSocket);
        clientHandler.handleCommand("ENTER testUser");
        verify(mockSocket, times(1)).getInputStream();
    }
}
