package Client;

import Message.MessageFromClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListener extends Thread{
    public static final int PORT = 10000;
    protected ServerSocket clientListenerServer;
    protected Socket clientListenerSocket;
    InputStream inFromServer;
    int number;

    public ClientListener(int number) {
        this.number = number;
    }

    public void run() {
        clientListenerServer = null;

        try {
            clientListenerServer = new ServerSocket(PORT + number);
            register();
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

                    MessageFromClient messageToServer = new MessageFromClient(sentence, clientListenerSocket, number);
                    messageToServer.answer();

                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void register() {
        Socket clientSocket = null;
        InetAddress inetAdress = null;
        InputStream inFromServer = null;
        OutputStream outToServer = null;

        try {
            inetAdress = InetAddress.getLocalHost();
            clientSocket = new Socket(inetAdress, PORT);
            inFromServer = clientSocket.getInputStream();
            outToServer = clientSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            //rejestracja klienta
            byte[] bytes = stringToByte("REGISTER " + number + " " + inetAdress.getHostAddress() + " " + clientSocket.getLocalPort() + '\n');
            outToServer.write(bytes);

            //odbiór potwierdzenia rejestracji
            byte[] byt = new byte[1024];
            inFromServer.read(byt);
            String sentence = byteToString(byt);
            System.out.println(sentence);

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
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
