package Server;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class Server extends Thread{
    // tworzę pustą listę z plikami hostów
    ArrayList fileList = new ArrayList();

    int number;
    String clientSentence;
    ServerSocket welcomeSocket;
    Socket connectionSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    ArrayList<String> users;
    ArrayList<String> files;

    public Server (int number, ArrayList<String> users) {
        this.number = number;
        this.users = users;
    }

    public void run() {
        welcomeSocket = null;

        //tworzenie gnaizda nasłuchującego
        try {
            welcomeSocket = new ServerSocket(10000);
        } catch (IOException exc) {
            System.out.println("Error: " + exc);
        }

        //połączenie klient-serwer
        try {
            while (true) {
                connectionSocket = welcomeSocket.accept();
                if(!welcomeSocket.isClosed()) {
                    inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    clientSentence = inFromClient.readLine();
                    System.out.println("Otrzymano od klienta: " + clientSentence);

                    //obsługa odebranego komunikatu
                    message(clientSentence);

                    //Wyświetlenie listy klientów
                    //System.out.println(users.toString());

                    //outToClient.close();
                    //inFromClient.close();
                }
            }

        } catch (Exception exc) {
            System.out.println("Error: " + exc);
        }
    }


    public void message(String sentence) {
        String[] messageComponents = clientSentence.split(" ");
        // tutaj jest logika, jak obsługiwać komunikaty
        switch (messageComponents[0]) {
            case "REGISTER":
                if(!hostExist(messageComponents)) {
                    String clients = messageComponents[1] + " " + messageComponents[2] + " " + messageComponents[3];
                    users.add(clients);
                    /*try{
                        outToClient.writeBytes("Zarejestrowany.");
                    } catch (IOException exc) {
                        System.out.println("Error: " + exc);
                    }*/
                }
                break;
            /*case "FILE":
                fileList.add(message.param);
                System.out.println(fileList);
                outToClient.writeBytes("ADDED " + message.param + '\n');
                files();
                break; */
            case "LIST":
                System.out.println("lista plikow");
                files();
                //outToClient.writeBytes(listFile.toString() + '\n');
                break;
            default:
                try {
                    outToClient.writeBytes("NOT RECOGNIZED COMMAND" + '\n');
                } catch (IOException exc) {
                    System.out.println("Error: " + exc);;
                }
                break;
        }
    }

    //sprawdzenie czy użytkownik jest zarejestrowany
    public boolean hostExist(String[] messageComponents) {
        boolean hostExist = false;
        for(int i = 0; i < users.size(); i++) {
            String[] numb = users.get(i).split(" ");
            if(messageComponents[1].equals(numb[0])) {
                hostExist = true;
            }
        }
        return hostExist;
    }

    //tworzenie listy plików
    public void files() {
        for(int i = 0; i < users.size(); i++) {
            InetAddress inetAdress = null;
            Socket server = null;
            outToClient = null;
            inFromClient = null;
            String[] numb = users.get(i).split(" ");
            int port = Integer.valueOf(numb[2]);
            //sprawdzenie połączenia z klientami
            try{
                System.out.println(port);
                inetAdress = InetAddress.getLocalHost();
                server = new Socket(inetAdress.getHostAddress(), port);
            } catch (IOException exc) {
                System.out.println("Error: " + exc);
            }

            //wysyłanie prośby o listy plików do każdego użytkownika
            //tworzenie listy trackera
            try{
                outToClient.writeBytes("LIST");
            } catch (IOException exc) {
                System.out.println("Error: " + exc);
            }

        }

    }
}