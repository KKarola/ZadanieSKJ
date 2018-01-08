package Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class MessageFromConsole {
    public static final int PORT = 10000;
    protected String sentence;
    protected int number;

    public MessageFromConsole(String sentence, int number) {
        this.sentence = sentence;
        this.number = number;
    }

    public void answer() {
        String[] messageComponents = sentence.split(" ");

        switch (messageComponents[0]) {
            case "LIST":
                list();
                break;
            case "PULL":
                pull(messageComponents[1], messageComponents[2]);
                break;
            case "PUSH":
                push(messageComponents[1], messageComponents[2]);
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

    public void list() {
        Socket socket = null;
        InputStream inFromServer = null;
        OutputStream outToServer = null;
        InetAddress inetAdress = null;
        try {
            socket = new Socket("127.0.0.1", PORT);
            inFromServer = socket.getInputStream();
            outToServer = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            byte[] bytes = stringToByte("LIST");
            outToServer.write(bytes);

            byte[] byt = new byte[1024];
            inFromServer.read(byt);
            String answer = byteToString(byt);
            System.out.println(answer);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void pull(String host, String fileName) {
        int port = Integer.parseInt(host);
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket("127.0.0.1", PORT + port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            byte[] bytes = stringToByte("PULL " + fileName);
            outputStream.write(bytes);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            File files = new File("D://TORrent_" + number + "//" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(files);
            byte[] bytesTab = new byte[1024];
            int count;
            while ((count = inputStream.read(bytesTab)) > 0) {
                fileOutputStream.write(bytesTab, 0, count);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void push(String host, String fileName) {
        int port = Integer.parseInt(host);
        Socket socket = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket("127.0.0.1", PORT + port);
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            byte[] bytes = stringToByte("PUSH " + fileName);
            outputStream.write(bytes);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            File transferFile = new File("D://TORrent_" + number + "//" + fileName);
            FileInputStream fileInputStream = new FileInputStream(transferFile);
            byte[] bytesTab = new byte[1024];
            int count;
            while ((count = fileInputStream.read(bytesTab)) > 0 ) {
                outputStream.write(bytesTab, 0, count);
            }
            outputStream.close();
            fileInputStream.close();
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
