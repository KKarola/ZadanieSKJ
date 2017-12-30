package Client;

import Message.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class ClientListenerConsole extends Thread {
    public static final int PORT = 10000;
    int number;

    public ClientListenerConsole(int number) { this.number = number; }

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
                BufferedReader userCommand = new BufferedReader(new InputStreamReader(System.in));
                String clientSentence = userCommand.readLine();
                DataOutputStream outToServer = new DataOutputStream(clientListenerConsole.getOutputStream());
                outToServer.writeBytes(clientSentence + '\n');

                ClientListenerServer clientListenerServer = new ClientListenerServer(number);
                clientListenerServer.start();


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
            clientSocket = new Socket("127.0.0.1", 10000);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes("REGISTER " + number + " " + inetAdress.getHostAddress() + " " + clientSocket.getLocalPort() + '\n');
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }
}
