package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Client extends Thread {
    int number;
    protected ServerSocket clientSocket;
    protected Socket client;
    DataOutputStream outToServer;
    BufferedReader inFromServer;
    BufferedReader userCommand;
    protected InetAddress inetAdress;
    protected String sentence;
    protected String modifiedSentence;
    File f;
    ArrayList<String> files;
    ArrayList<String> listFile;

    public Client (int number) { this.number = number; }

    public void run() {
        clientSocket = null;

        //tworzenie gniazda, rejestracja klientów
        try {
            clientSocket = new ServerSocket(0);
            //list();
            register();
        } catch (Exception exc) {
            System.out.println("Error: " + exc);
        }

        //wysyłanie zapytań do serwera
        try {
            while(true) {
                client = clientSocket.accept();
                if(!clientSocket.isClosed()) {
                    //strumienie wejścia/wyjścia
                    outToServer = new DataOutputStream(client.getOutputStream());
                    inFromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    userCommand = new BufferedReader(new InputStreamReader(System.in));
                    //wysłanie komunikatu do serwera
                    sentence = userCommand.readLine();
                    outToServer.writeBytes(sentence + '\n');

                    // obsługa odebranego komunikatu
                    message(sentence);

                    modifiedSentence = inFromServer.readLine();
                    System.out.println("FROM SERVER: " + modifiedSentence);

                    //outToServer.close();
                    //inFromServer.close();
                } else {
                    System.out.println("Client Socket is closed.");
                }
            }
        } catch (Exception exc) {
            System.out.println("Error: " + exc);
        }
    }

    public void message(String sentence) {
        String[] messageComponents = sentence.split(" ");
        // tutaj jest logika, jak obsługiwać komunikaty
        switch (messageComponents[0]) {
            /*case "FILE":
                fileList.add(message.param);
                System.out.println(fileList);
                outToClient.writeBytes("ADDED " + message.param + '\n');
                break;*/
            case "LIST":
                System.out.println("lista plikow");
                list();
                for (int i = 0; i < listFile.size(); i++) {
                    try {
                        outToServer.writeBytes(listFile.get(i) + '\n');
                    } catch (IOException exc) {
                        System.out.println("Error: " + exc);
                    }
                }
                /*try{
                    outToServer.writeBytes("Koniec listy.");
                } catch (IOException exc) {
                    System.out.println("Error: " + exc);
                }*/
                //outToServer.writeBytes(listFile.toString() + '\n');   */
                break;
            default:
                try {
                    outToServer.writeBytes("NOT RECOGNIZED COMMAND" + '\n');
                } catch (IOException exc) {
                    System.out.println("Error: " + exc);;
                }
                break;
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
                System.out.println("Error: " + exc);
            }

            if (files.size() == 0) {
                System.out.println("The directory is empty");
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
                        listFile.add(files.get(i) + "\t" + sb.toString());
                    } catch (NoSuchAlgorithmException exc) {
                        System.out.println("Error: " + exc);
                    } catch (FileNotFoundException exc) {
                        System.out.println("Error: " + exc);
                    } catch (IOException exc) {
                        System.out.println("Error: " + exc);
                    }
                }
            }
        } catch (NullPointerException exc) {
            System.out.println("Error: " + exc);
        }
    }

    public void register() {
        Socket client = null;
        InetAddress inetAdress = null;
        outToServer = null;
        inFromServer = null;
        userCommand = null;

        //tworzenie gniazda
        try {
            inetAdress = InetAddress.getLocalHost();
            client = new Socket(inetAdress, 10000);
        } catch (IOException exc) {
            System.out.println("Error: " + exc);
        }

        //połączenie klient-serwer
        //while (true) {
            try {
                outToServer = new DataOutputStream(client.getOutputStream());
                //inFromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));

                outToServer.writeBytes("REGISTER" + number + " " + inetAdress.getHostAddress() + " " + client.getLocalPort());
                //System.out.println(inFromServer.readLine());
            } catch (IOException exc){
                System.out.println("Error: " + exc);
            }
        //}

        /*try {
            client.close();
        } catch (IOException exc) {
            System.out.println("Error " + exc);
        }
*/
    }

}