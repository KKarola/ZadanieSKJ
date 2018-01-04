package Message;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageToServer {
    protected String sentence;
    protected Socket socket;
    protected int number;
    protected DataOutputStream outToServer;
    protected File file;
    protected ArrayList<String> files;
    protected ArrayList<String> listFile;

    public MessageToServer(String sentence, Socket socket, int number) {
        this.sentence = sentence;
        this.socket = socket;
        this.number = number;
    }

    public void answer() {
        String[] messageComponents = this.sentence.split(" ");
        try {
            outToServer = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        switch (messageComponents[0]) {
            case "LIST":
                try {
                    listFileMD5();
                    byte[] bytes;
                    bytes = stringToByte(Integer.toString(listFile.size()));
                    outToServer.write(bytes);

                    for (int i = 0; i < listFile.size(); i++) {
                        byte[] bytesTab;
                        bytesTab = stringToByte(listFile.get(i).toString());
                        outToServer.write(bytesTab);
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e);
                }
                break;
        }
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

    // https://stackoverflow.com/questions/7301764/how-to-get-contents-of-a-folder-and-put-into-an-arraylist
    //tworzenie listy plików znajdujących się w podanym katalogu
    public void listFile() {
        try{
            file = new File("D://TORrent_" + number);
            files = new ArrayList<String>(Arrays.asList(file.list()));
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
        }
    }

    // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
    // obliczanie sumy kontrolnej dla wszystkich plików w katalogu
    public void listFileMD5 () {
        listFile = new ArrayList<String>();
        listFile();
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
                listFile.add(files.get(i) + " " + sb.toString());
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Error: " + e);
            } catch (FileNotFoundException e) {
                System.out.println("Error: " + e);
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }



}