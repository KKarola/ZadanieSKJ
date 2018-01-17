package Message;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessageFromServer {
    public static final int PORT = 10000;
    protected ArrayList<String> users;
    protected ArrayList<String> fileList;
    protected String sentence;
    protected Socket socket;
    protected OutputStream outToClient;

    public MessageFromServer(String sentence, Socket socket, ArrayList<String> users) {
        this.sentence = sentence;
        this.socket = socket;
        this.users = users;
    }

    public void answer() {
        try {
            outToClient = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        String[] messageComponents = this.sentence.split(" ");
        switch (messageComponents[0]) {
            case "REGISTER":
                register(messageComponents);
                break;
            case "LIST":
                sendList();
                break;
            case "EXIT":
                exit(messageComponents[1]);
                break;
        }
    }

    public byte[] stringToByte(String s) {
        byte[] buffer = new byte[1024];
        byte[] bytes = String.valueOf(s).getBytes();
        for (int i = 0; i < bytes.length; i++) {
            buffer[i] = bytes[i];
        }
        return buffer;
    }

    public String byteToString(byte buffer[]) {
        String date = new String(buffer, 0, buffer.length).trim();
        return date;
    }

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

    public void register(String[] messageComponents) {
        if (!hostExist(messageComponents)) {
            try {
                int number = Integer.parseInt(messageComponents[1]);
                String clients = messageComponents[1] + " " + messageComponents[2] + " " + messageComponents[3];
                users.add(clients);
                byte[] bytes = stringToByte("Registered successfully - number " + Integer.toString(number));
                outToClient.write(bytes);
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        } else {
            try {
                byte[] bytes = stringToByte("The client exists.");
                outToClient.write(bytes);
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void files() {
        fileList = new ArrayList<String>();
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
                byte[] bytes = stringToByte("LIST");
                outputStream.write(bytes);

                byte[] byt = new byte[1024];
                inputStream.read(byt);
                String sentence = byteToString(byt);

                for (int j = 0; j < Integer.parseInt(sentence); j++) {
                    byte[] bytesTab = new byte[1024];
                    inputStream.read(bytesTab);
                    fileList.add(Integer.toString(number) + " " + byteToString(bytesTab));
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

    public void sendList() {
        files();
        try {
            byte[] bytes = stringToByte(Integer.toString(fileList.size()));
            outToClient.write(bytes);
            for (int i = 0; i < fileList.size(); i++) {
                byte[] byt = stringToByte(fileList.get(i));
                outToClient.write(byt);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void exit(String messageComponent) {
        try {
            for(int i = 0; i < users.size(); i++) {
                String[] number = users.get(i).split(" ");
                if(number[0].equals(messageComponent)) {
                    users.remove(i);
                    byte[] bytes = stringToByte("Unregistered successfully.");
                    outToClient.write(bytes);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

}
