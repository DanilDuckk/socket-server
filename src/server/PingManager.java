package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import messages.ping_pong.Hangup;
import messages.ping_pong.PongError;
import util.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class PingManager {
    private Timer pingTimer;
    private boolean awaitingPong;
    private PrintWriter writer;
    private Socket socket;
    private String username;

    public PingManager(PrintWriter writer, Socket socket, String username) {
        this.writer = writer;
        this.socket = socket;
        this.username = username;
    }

    public void startPingTimer() {
        pingTimer = new Timer(true);
        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (awaitingPong) {
                    sendHangupMessage(3000);
                    disconnectClient();
                } else {
                    sendPingMessage();
                }
            }
        }, 10000, 10000);
    }

    private void sendPingMessage() {
        awaitingPong = true;
        writer.println("PING");
        writer.flush();

        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                if (awaitingPong) {
                    sendHangupMessage(3000);
                    disconnectClient();
                }
            }
        }, 3000);
    }

    public void sendPongError(int errorCode) {
        try {
            writer.println(ObjectMapper.objectToMessage(new PongError(errorCode)));
            writer.flush();
        } catch (JsonProcessingException e) {
            System.err.println("Failed to send PONG_ERROR message: " + e.getMessage());
        }
    }

    public void stopPingTimer() {
        if (pingTimer != null) {
            pingTimer.cancel();
        }
    }

    private void sendHangupMessage(int reasonCode) {
        try {
            writer.println(ObjectMapper.objectToMessage(new Hangup(reasonCode)));
            writer.flush();
        } catch (JsonProcessingException e) {
            System.err.println("Failed to send HANGUP message: " + e.getMessage());
        }
    }

    public boolean isAwaitingPong() {
        return awaitingPong;
    }

    public void setIsAwaitingPongFalse() {
        awaitingPong = false;
    }

    public void disconnectClient() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket for user: " + username);
        }
    }
}
