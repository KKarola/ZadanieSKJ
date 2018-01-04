package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientListenerConsole extends Thread {
    public static final int PORT = 10000;
    int number;

    public ClientListenerConsole(int number) {
        this.number = number;
        ClientListenerServer clientListenerServer = new ClientListenerServer(this.number);
        clientListenerServer.start();
    }

    public void run() {
        Socket clientListenerConsole = null;

        try {
            register();
            clientListenerConsole = new Socket("127.0.0.1", PORT);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        while (true) {
            try {
                InputStream userCommand = System.in;
                DataOutputStream outToServer = new DataOutputStream(clientListenerConsole.getOutputStream());

                //wysłanie żądania wczytanego z konsoli
                byte[] byt = new byte[1024];
                userCommand.read(byt);
                String sentence = byteToString(byt);
                byte[] bytes = stringToByte(sentence);
                outToServer.write(bytes);

                //odbiór odpowiedzi od serwera
                InputStream inFromServer = clientListenerConsole.getInputStream();
                byte[] b = new byte[1024];
                inFromServer.read(b);
                String sentences = byteToString(b);
                System.out.println(sentences);

            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void register() {
        Socket clientSocket = null;
        InetAddress inetAdress = null;
        DataOutputStream outToServer = null;

        try {
            inetAdress = InetAddress.getLocalHost();
            clientSocket = new Socket("127.0.0.1", PORT);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            //rejestracja klienta
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            byte[] bytes = stringToByte("REGISTER " + number + " " + inetAdress.getHostAddress() + " " + clientSocket.getLocalPort() + '\n');
            outToServer.write(bytes);

            //odbiór potwierdzenia rejestracji
            InputStream inFromServer = clientSocket.getInputStream();
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
