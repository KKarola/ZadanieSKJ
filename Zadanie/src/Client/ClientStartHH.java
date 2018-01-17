package Client;

public class ClientStartHH extends Thread{
    int number;

    public ClientStartHH(int number) {
        this.number = number;
    }

    public void run() {
        ClientListenerConsoleHH clientListenerConsole = new ClientListenerConsoleHH(number);
        clientListenerConsole.start();
        ClientListenerHH clientListener = new ClientListenerHH(number);
        clientListener.start();
    }

}
