package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerListener extends Thread {
    public static final int PORT = 10000;
    protected Socket connectionSocket;
    protected ArrayList<String> users = new ArrayList<>();

    public void run() {
        ServerSocket welcomeSocket = null;

        try {
            welcomeSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        while (true) {
            try {
                connectionSocket = welcomeSocket.accept();
                if(!welcomeSocket.isClosed()) {
                    ServerSender serverSender = new ServerSender(connectionSocket, users);
                    serverSender.start();
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }
}
