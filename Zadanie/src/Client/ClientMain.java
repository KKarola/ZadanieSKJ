package Client;

public class ClientMain {
    public static void main(String[] args) {
        ClientListenerConsole clientListenerConsole = new ClientListenerConsole(1);
        clientListenerConsole.start();
    }
}
