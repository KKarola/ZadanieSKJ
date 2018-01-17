package Client;

import Message.MessageFromClientHH;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListenerHH extends Thread{
    public static final int PORT = 10000;
    protected ServerSocket clientListenerServer;
    protected Socket clientListenerSocket;
    protected InputStream inFromServer;
    protected int number;

    public ClientListenerHH(int number) {
        this.number = number;
    }

    public void run() {
        clientListenerServer = null;

        try {
            clientListenerServer = new ServerSocket(PORT + number);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        while (true) {
            try {
                clientListenerSocket = clientListenerServer.accept();
                if(!clientListenerServer.isClosed()) {
                    inFromServer = clientListenerSocket.getInputStream();

                    byte[] bytes = new byte[1024];
                    inFromServer.read(bytes);
                    String sentence = byteToString(bytes);

                    MessageFromClientHH messageToServer = new MessageFromClientHH(sentence, clientListenerSocket, number);
                    messageToServer.answer();

                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public byte[] stringToByte(String s) {
        byte[] buffer = new byte[1024];
        byte[] bytes = String.valueOf(s).getBytes();
        for (int i = 0; i < bytes.length; i++) {
            buffer[i] = bytes[i];
        }
        return buffer;
    }

    public String byteToString(byte buffer[]) {
        String date = new String(buffer, 0, buffer.length).trim();
        return date;
    }

}
