package Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessageToClient {
    public static final int PORT = 10000;
    protected ArrayList<String> users;
    protected ArrayList<String> fileList;
    protected String sentence;
    protected int number;
    protected Socket socket;
    protected DataOutputStream outToClient;

    public MessageToClient(String sentence, Socket socket, ArrayList<String> users) {
        this.sentence = sentence;
        this.socket = socket;
        this.users = users;
    }

    public void answer() {
        String[] messageComponents = this.sentence.split(" ");
        try {
            outToClient = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        switch (messageComponents[0]) {
            case "REGISTER":
                if (!hostExist(messageComponents)) {
                    try {
                        number = Integer.parseInt(messageComponents[1]);
                        String clients = messageComponents[1] + " " + messageComponents[2] + " " + messageComponents[3];
                        users.add(clients);
                        byte[] bytes = stringToByte("Zarejestrowano klienta o numerze " + Integer.toString(number));
                        outToClient.write(bytes);
                    } catch (IOException e) {
                        System.out.println("Error: " + e);
                    }
                } else {
                    try {
                        byte[] bytes = stringToByte("Klient o podanym numerze już istnieje.");
                        outToClient.write(bytes);
                    } catch (IOException e) {
                        System.out.println("Error: " + e);
                    }
                }
                break;
            case "LIST":
                try {
                    files();
                    byte[] bytes;
                    bytes = stringToByte(fileList.toString());
                    outToClient.write(bytes);
                } catch (IOException e) {
                    System.out.println("Error: " + e);
                }
                break;
        }
    }

    //sprawdzenie czy użytkownik jest zarejestrowany
    public boolean hostExist(String[] messageComponents) {
        boolean hostExist = false;
        for (int i = 0; i < users.size(); i++) {
            String[] numb = users.get(i).split(" ");
            if (messageComponents[1].equals(numb[0])) {
                hostExist = true;
            }
        }
        return hostExist;
    }

    //zamiana Stringa na byte
    public byte[] stringToByte(String s) {
        byte[] buffer = new byte[1024];
        byte[] bytes = String.valueOf(s).getBytes();
        for (int i = 0; i < bytes.length; i++) {
            buffer[i] = bytes[i];
        }
        return buffer;
    }

    //zapisanie byte do Stringa
    public String byteToString(byte buffer[]) {
        String date = new String(buffer, 0, buffer.length).trim();
        return date;
    }

    //tworzenie listy plików dostępnianych przez poszczególne hosty
    public void files() {
        fileList = new ArrayList<String>();
        //sprawdzanie połączeń server-host
        for (int i = 0; i < users.size(); i++) {
            String[] usersComponent = users.get(i).split(" ");
            int number = Integer.parseInt(usersComponent[0]);
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                socket = new Socket("127.0.0.1", PORT + number);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (UnknownHostException e) {
                System.out.println("Error: " + e);
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }

            try {
                //wysyłamy prośbę o listę plików
                byte[] bytes = stringToByte("LIST");
                outputStream.write(bytes);

                //odpowiedź klienta
                //ile plików ma w katalogu
                byte[] byt = new byte[1024];
                inputStream.read(byt);
                String sentence = byteToString(byt);

                //pobranie plików wraz z sumami kontrolnymi
                for (int j = 0; j < Integer.parseInt(sentence); j++) {
                    byte[] bytesTab = new byte[1024];
                    inputStream.read(bytesTab);
                    String sentence1 = byteToString(bytesTab);
                    fileList.add(Integer.toString(number) + " " + sentence1);
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }

            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

}
