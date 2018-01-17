package Client;

import Config.Config;
import Message.MessageFromClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListener extends Thread{
    int port = Config.INSTANCE.getPort();
    String ip = Config.INSTANCE.getIp();
    int sizeOfPacket = Config.INSTANCE.getSizeOfPacket();
    protected ServerSocket clientListenerServer;
    protected Socket clientListenerSocket;
    protected InputStream inFromServer;
    protected int number;

    public ClientListener(int number) {
        this.number = number;
    }

    public void run() {
        clientListenerServer = null;

        try {
            clientListenerServer = new ServerSocket(port + number);
            register();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        while (true) {
            try {
                clientListenerSocket = clientListenerServer.accept();
                if(!clientListenerServer.isClosed()) {
                    inFromServer = clientListenerSocket.getInputStream();

                    byte[] bytes = new byte[sizeOfPacket];
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

    public byte[] stringToByte(String s) {
        byte[] buffer = new byte[sizeOfPacket];
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

    public void register() {
        Socket clientSocket = null;
        InputStream inFromServer = null;
        OutputStream outToServer = null;

        try {
            clientSocket = new Socket(ip, port);
            inFromServer = clientSocket.getInputStream();
            outToServer = clientSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            byte[] bytes = stringToByte("REGISTER " + number + " " + ip + " " + clientSocket.getLocalPort() + '\n');
            outToServer.write(bytes);

            byte[] byt = new byte[sizeOfPacket];
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

}
