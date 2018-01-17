package Client;

import Config.Config;
import Message.MessageFromClientHH;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListenerHH extends Thread{
    int port = Config.INSTANCE.getPort();
    String ip = Config.INSTANCE.getIp();
    int sizeOfPacket = Config.INSTANCE.getSizeOfPacket();
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
            clientListenerServer = new ServerSocket(port + number);
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

                    MessageFromClientHH messageToServer = new MessageFromClientHH(sentence, clientListenerSocket, number);
                    messageToServer.answer();

                }
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
