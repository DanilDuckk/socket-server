import static org.mockito.Mockito.*;

import client.Client;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.*;

class ClientTest {

    private Client client;
    private Socket mockSocket;
    private InputStream mockInputStream;
    private OutputStream mockOutputStream;

    @BeforeEach
    void setUp() throws IOException {
        mockSocket = mock(Socket.class);
        mockInputStream = mock(InputStream.class);
        mockOutputStream = mock(OutputStream.class);
        client = new Client();

        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        client.setupConnection();
    }

    @Test
    void testSetupConnection() throws IOException {
        client.setupConnection();
        verify(mockSocket).getInputStream();
        verify(mockSocket).getOutputStream();
    }

    @Test
    void testSetupOutput() {
    }

    @Test
    void testHandleIncomingMessages_Ping() throws IOException {
        String pingMessage = "PING";
        BufferedReader mockReader = mock(BufferedReader.class);
        when(mockReader.readLine()).thenReturn(pingMessage);

        client.setupInput();

        verify(mockOutputStream, times(1)).write(any(byte[].class));
    }
}
