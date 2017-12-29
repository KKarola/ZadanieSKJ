package Server;

import java.util.ArrayList;

public class ServerMain {
    public static void main(String[] args) {
       ArrayList<String> hostList=new ArrayList<>();

        Server server = new Server(1, hostList);
        server.start();
    }
}
