package Client;

public class ClientMain {
    public static void main(String[] args) {
        /*Client client = new Client(Integer.parseInt(args[0]));
        client.start();*/

        ClientListenerConsole clientListenerConsole = new ClientListenerConsole(1);
        clientListenerConsole.start();
    }
}
