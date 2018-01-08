package Message;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageFromClient {
    protected String sentence;
    protected Socket socket;
    protected int number;
    protected OutputStream outputStream;
    protected InputStream inputStream;
    protected File file;
    protected ArrayList<String> files;
    protected ArrayList<String> listFile;

    public MessageFromClient(String sentence, Socket socket, int number) {
        this.sentence = sentence;
        this.socket = socket;
        this.number = number;
    }

    public void answer() {
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        String[] messageComponents = sentence.split(" ");
        switch (messageComponents[0]) {
            case "LIST":
                listFileMD5();
                sendList();
                break;
            case "PULL":
                sendFile(messageComponents[1]);
                break;
            case "PUSH":

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

    public void listFile() {
        try{
            file = new File("D://TORrent_" + number);
            files = new ArrayList<String>(Arrays.asList(file.list()));
        } catch (NullPointerException e) {
            System.out.println("Error: " + e);
        }
    }

    // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
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

    public void sendList() {
        try {
            byte[] bytes;
            bytes = stringToByte(Integer.toString(listFile.size()));
            outputStream.write(bytes);

            for (int i = 0; i < listFile.size(); i++) {
                byte[] bytesTab;
                bytesTab = stringToByte(listFile.get(i).toString());
                outputStream.write(bytesTab);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void sendFile(String fileName) {
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
    }

}
