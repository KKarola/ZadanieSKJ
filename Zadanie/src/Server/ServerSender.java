package Server;

import Message.MessageFromServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSender extends Thread {
    protected Socket connectionClient;
    protected ArrayList<String> users;
    InputStream inputStream;

    public ServerSender (Socket connectionClient, ArrayList<String> users) {
        this.connectionClient = connectionClient;
        this.users = users;
    }

    public void run() {
        while (true) {
            try{
                inputStream = connectionClient.getInputStream();
                byte[] bytes = new byte[1024];
                inputStream.read(bytes);
                String sentence = byteToString(bytes);

                MessageFromServer message = new MessageFromServer(sentence, connectionClient, users);
                message.answer();

            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public String byteToString(byte buffer[]) {
        String date = new String(buffer, 0, buffer.length).trim();
        return date;
    }
}
