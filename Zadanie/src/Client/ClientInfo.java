package Client;
/*
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientInfo extends Thread {
    int number;

    public ClientInfo(int number) {
        this.number = number;
    }

    public void run() {
        new ClientListenerConsole(number);
        new ClientListenerServer(number);
        //register();
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
*/