package Server;

import Message.MessageToClient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSender extends Thread {
    protected Socket connectionClient;
    protected ArrayList<String> users;

    public ServerSender (Socket connectionClient, ArrayList<String> users) {
        this.connectionClient = connectionClient;
        this.users = users;
    }

    public void run() {
        while (true) {
            try{
                InputStream inFromClient = connectionClient.getInputStream();
                byte[] bytes = new byte[1024];
                inFromClient.read(bytes);
                String sentence = byteToString(bytes);

                MessageToClient message = new MessageToClient(sentence, connectionClient, users);
                message.answer();

            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    //zamiana Stringa na byte
    public byte[] stringToByte(String s) {
        byte[] buffer = new byte[1024];
        byte[] bytes = String.valueOf(s).getBytes();
        for (int i = 0; i < bytes.length; i++) {
            buffer[i] = bytes[i];
        }
        return buffer;
    }

    //zapisanie byte do Stringa
    public String byteToString(byte buffer[]) {
        String date = new String(buffer, 0, buffer.length).trim();
        return date;
    }
}
