package Server;

import Message.Message;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerSender extends Thread {
    Socket connectionClient;

    public ServerSender (Socket connectionClient) { this.connectionClient = connectionClient; }

    public void run() {

        while (true) {
            try{
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionClient.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionClient.getOutputStream());
                String clientSentence = inFromClient.readLine();

                System.out.println("Otrzymano od klienta: " + clientSentence);

                //Message message = new Message(clientSentence);

            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }
}
