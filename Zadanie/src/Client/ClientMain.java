package Client;

public class ClientMain {
    public static void main(String[] args) {
        ClientStart clientStart = new ClientStart(Integer.parseInt(args[0]));
        clientStart.start();
    }
}
