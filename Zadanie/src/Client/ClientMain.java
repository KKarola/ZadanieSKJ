package Client;

public class ClientMain {
    public static void main(String[] args) {
        ClientListenerConsole clientListenerConsole = new ClientListenerConsole(2);
        clientListenerConsole.start();

    }
}
