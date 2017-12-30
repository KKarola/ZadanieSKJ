import java.util.Vector;

public class ServerMain {
    public static void main(String[] args) {
        Vector<String[]> hostVector=new Vector<>();
        ServerListenThread server = new ServerListenThread(1, hostVector);
        server.start();

    }
}
