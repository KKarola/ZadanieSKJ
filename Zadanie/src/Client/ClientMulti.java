package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/*public class ClientMulti extends Thread {
    int number;
    Socket clientSocket;
    ServerSocket client;
    DataOutputStream outToServer;
    BufferedReader inFromServer;
    BufferedReader userCommand;
    String sentence;
    String modifiedSentence;
    File f;
    ArrayList<String> files;
    ArrayList<String> listFile;

    public ClientMulti (int number) { this.number = number; }

    public void run() {
        //clientSocket = null;
        client = null;

        //tworzenie gniazda
        try {
            //nie działa accept();
            //clientSocket = new Socket("127.0.0.1", 10000);
            client = new ServerSocket(0);
            register();
        } catch (IOException exc) {
            System.out.println("Blad: " + exc);
        }

        //wysyłanie zapytań
        try {
            while(true) {
                //strumienie wejścia/wyjścia
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                userCommand = new BufferedReader(new InputStreamReader(System.in));
                //wysłanie komunikatu do pobrania listy plików: "LIST"
                sentence =  userCommand.readLine();
                outToServer.writeBytes(sentence + '\n');
                modifiedSentence = inFromServer.readLine();
                System.out.println("FROM SERVER: " + modifiedSentence);

                // zapisanie odebranego komunikatu jako obiekt Message
                Message message = new Message(sentence);

                // tutaj jest logika, jak obsługiwać komunikaty
                switch (message.type) {
                    case "FILE":
                        /*fileList.add(message.param);
                        System.out.println(fileList);
                        outToClient.writeBytes("ADDED " + message.param + '\n');
                        break;
                    case "LIST":
                        System.out.println("lista plikow");
                        list();
                        outToServer.writeBytes(listFile.toString() + '\n');
                        break;
                    default:
                        //defautToClient.writeBytes("NOT RECOGNIZED COMMAND" + '\n');
                        break;
                }
            }
        } catch (Exception exc) {
            System.out.println("Blad: " + exc);
        }
    }

    public void list() {
        try {
            // https://stackoverflow.com/questions/7301764/how-to-get-contents-of-a-folder-and-put-into-an-arraylist
            // tworzenie listy plików znajdujących się w katalogu - ArrayList
            try{
                f = new File("D://TORrent_" + number);
                files = new ArrayList<String>(Arrays.asList(f.list()));
                listFile = new ArrayList<String>();
            } catch (NullPointerException exc) {
                System.out.println("Blad: " + exc);
            }

            if (files.size() == 0) {
                System.out.println("Katalog jest pusty");
            } else {
                // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
                // obliczanie sumy kontrolnej dla wszystkich plików w katalogu
                for (int i = 0; i < files.size(); i++) {
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        FileInputStream fis = new FileInputStream("D://TORrent_" + number + "//" + files.get(i));
                        byte[] dataBytes = new byte[1024];
                        int nread = 0;
                        while ((nread = fis.read(dataBytes)) != -1) {
                            md.update(dataBytes, 0, nread);
                        };
                        fis.close();
                        byte[] mdbytes = md.digest();
                        StringBuffer sb = new StringBuffer();
                        for (int j = 0; j < mdbytes.length; j++) {
                            sb.append(Integer.toString((mdbytes[j] & 0xff) + 0x100, 16).substring(1));
                        }
                        listFile.add((InetAddress.getLocalHost().getHostName() + "\t" + files.get(i) + "\t" + sb.toString()).toString());
                    } catch (NoSuchAlgorithmException exc) {
                        System.out.println("Blad: " + exc);
                    } catch (FileNotFoundException exc) {
                        System.out.println("Blad: " + exc);
                    } catch (IOException exc) {
                        System.out.println("Blad: " + exc);
                    }
                }
            }
        } catch (NullPointerException exc) {
            System.out.println("Blad: " + exc);
        }
    }

    public void register() {
        Socket socket = null;
        DataOutputStream out = null;
        BufferedReader in = null;
        InetAddress inetAddress = null;
        int port = 10000;

        try {
            inetAddress = InetAddress.getLocalHost();
            socket = new Socket(inetAddress.getHostAddress(), port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException exc) {
            System.out.println("Blad " + exc);
        } catch (IOException exc) {
            System.out.println("Blad " + exc);
        } catch (IllegalArgumentException exc) {
            System.out.println("Blad " + exc);
        }



    }

}
*/