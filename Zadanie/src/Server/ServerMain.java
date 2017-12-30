package Server;

import java.util.ArrayList;

public class ServerMain {
    public static void main(String[] args) {
        /*Server server = new Server();
        server.start();*/

        ServerListener serverListener = new ServerListener();
        serverListener.start();
    }
}
