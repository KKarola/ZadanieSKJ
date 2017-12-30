package Client;

import Message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListenerServer extends Thread{
    public static final int PORT = 10000;
    int number;

    public ClientListenerServer(int number) { this.number = number; }

    public void run() {
        ServerSocket clientListenerServer = null;

        try {
            clientListenerServer = new ServerSocket(PORT + number);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        Socket clientListenerSocket = null;
        while (true) {
            try {
                clientListenerSocket = clientListenerServer.accept();
                if(clientListenerServer.isClosed()) {
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientListenerSocket.getInputStream()));
                    String serverSentence = inFromServer.readLine();
                    //Message message = new Message(serverSentence);
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }
}
