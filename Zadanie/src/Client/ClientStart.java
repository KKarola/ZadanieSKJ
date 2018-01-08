package Client;

public class ClientStart extends Thread{
    int number;

    public ClientStart(int number) {
        this.number = number;
    }

    public void run() {
        ClientListenerConsole clientListenerConsole = new ClientListenerConsole(number);
        clientListenerConsole.start();
        ClientListener clientListener = new ClientListener(number);
        clientListener.start();
    }

}
