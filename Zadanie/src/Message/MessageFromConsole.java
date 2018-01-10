package Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MessageFromConsole {
    public static final int PORT = 10000;
    protected String sentence;
    protected int number;
    protected String[] fileTab;

    public MessageFromConsole(String sentence, int number) {
        this.sentence = sentence;
        this.number = number;
    }

    public void answer() {
        String[] messageComponents = sentence.split(" ");

        switch (messageComponents[0]) {
            case "LIST":
                list();
                for (int i = 0; i < fileTab.length; i++)
                    System.out.println(fileTab[i]);
                break;
            case "PULL":
                pull(messageComponents[1], messageComponents[2]);
                break;
            case "PUSH":
                push(messageComponents[1], messageComponents[2]);
                break;
            case "PULL_CONTINUE":
                pull_continue(messageComponents[1], messageComponents[2]);
                break;
            case "PUSH_CONTINUE":
                push_contiune(messageComponents[1], messageComponents[2]);
                break;
            case "PULL_FROM_MANY":
                list();
                pull_from_many(messageComponents[1], messageComponents[2]);
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
            int amount = Integer.valueOf(byteToString(byt));

            fileTab = new String[amount];
            byte[] bytesTab = new byte[1024];
            String answer;
            for (int i = 0; i < amount; i++) {
                inFromServer.read(bytesTab);
                answer = byteToString(bytesTab);
                fileTab[i] = answer;
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
            File file = new File("D://TORrent_" + number + "//" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
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
            File file = new File("D://TORrent_" + number + "//" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
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

    public void pull_continue(String host, String fileName) {
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
            File file = new File("D://TORrent_" + number + "//" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String fileLen = Long.toString(file.length());
            byte[] bytes = stringToByte("PULL_CONTINUE " + fileName + " " + fileLen);
            outputStream.write(bytes);

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

    public void push_contiune (String host, String fileName) {
        int port = Integer.parseInt(host);
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            socket = new Socket("127.0.0.1", PORT + port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            byte[] bytes = stringToByte("PUSH_CONTINUE " + fileName);
            outputStream.write(bytes);

            byte[] bytesTab = new byte[1024];
            inputStream.read(bytesTab);
            String send = byteToString(bytesTab);
            long fileLen = Long.parseLong(send);

            File file = new File("D://TORrent_" + number + "//" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.skip(fileLen);
            byte[] byt = new byte[1024];
            int count;
            while ((count = fileInputStream.read(byt)) > 0 ) {
                outputStream.write(byt, 0, count);
            }
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
    }

    public void pull_from_many (String fileName, String checksum) {
        String[] sentence;
        ArrayList<String> host = new ArrayList<>();
        for (int i = 0; i < fileTab.length; i++) {
            sentence = fileTab[i].split(" ");
            if(sentence[1].equals(fileName) && sentence[2].equals(checksum)) host.add(sentence[0]);
        }

        try {
            File file = new File("D://TORrent_" + number + "//" + fileName);
            RandomAccessFile fileOutputStream = new RandomAccessFile(file, "rw");
            FileChannel fileChannel = fileOutputStream.getChannel();

            Thread[] multi = new Thread[host.size()];
            for (int i = 0; i < host.size(); i++) {
                int number = Integer.parseInt(host.get(i));
                multi[i] = new Thread(new Multi(fileOutputStream, number, fileName, host.size(), i, fileChannel));
                multi[i].start();
            }

            for (int i = 0; i < host.size(); i++) {
                multi[i].join();
            }
            fileOutputStream.close();

            checksum(fileName);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void checksum(String fileName) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream("D://TORrent_" + number + "//" + fileName);
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
            System.out.println(fileName + " " + sb.toString());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: " + e);
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
