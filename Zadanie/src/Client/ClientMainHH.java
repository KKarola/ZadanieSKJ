package Client;

public class ClientMainHH {
    public static void main(String[] args) {
        ClientStartHH clientStart = new ClientStartHH(Integer.parseInt(args[0]));
        clientStart.start();
    }
}
