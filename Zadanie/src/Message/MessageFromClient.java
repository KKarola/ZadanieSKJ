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
                receiveFile(messageComponents[1]);
                break;
            case "PULL_CONTINUE":
                reSendFile(messageComponents[1], messageComponents[2]);
                break;
            case "PUSH_CONTINUE":
                reReceiveFile(messageComponents[1]);
                break;
            case "SIZE":
                checkSize(messageComponents[1]);
                break;
            case "MULTI":
                multi(messageComponents[1], messageComponents[2], messageComponents[3]);
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
            listFile.add(fileName + " " + sb.toString());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: " + e);
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void listFileMD5 () {
        listFile = new ArrayList<String>();
        listFile();
        for (int i = 0; i < files.size(); i++)
            checksum(files.get(i));
    }

    public void sendList() {
        try {
            byte[] bytes = stringToByte(Integer.toString(listFile.size()));
            outputStream.write(bytes);

            for (int i = 0; i < listFile.size(); i++) {
                byte[] bytesTab = stringToByte(listFile.get(i).toString());
                outputStream.write(bytesTab);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void sendFile(String fileName) {
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
    }

    public void receiveFile(String fileName) {
        try {
            File file = new File("D://TORrent_" + number + "//" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bytesTab = new byte[1024];
            int count;
            while ((count = inputStream.read(bytesTab)) > 0) {
                fileOutputStream.write(bytesTab, 0, count);
            }
            fileOutputStream.close();
            if (file.length() == 0) file.delete();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void reSendFile(String fileName, String fileLen){
        try {
            File file = new File("D://TORrent_" + number + "//" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            long send = Long.parseLong(fileLen);
            fileInputStream.skip(send);
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

    public void reReceiveFile (String fileName) {
        try {
            File file = new File("D://TORrent_" + number + "//" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String receiveLen = Long.toString(file.length());
            byte[] bytes = stringToByte(receiveLen);
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
    }

    public void checkSize (String fileName) {
        try {
            File file = new File("D://TORrent_" + number + "//" + fileName);
            String receivePackage = Long.toString(file.length());
            byte[] bytes = stringToByte(receivePackage);
            outputStream.write(bytes);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void multi(String fileName, String amountOfHosts, String yourNumber) {
        try {
            File file = new File("D://TORrent_" + number + "//" + fileName);
            RandomAccessFile fileInputStream = new RandomAccessFile(file, "rw");
            int amountHost = Integer.parseInt(amountOfHosts);
            int numb = Integer.parseInt(yourNumber);

            int packetSize = (int) file.length()/1024;
            if((file.length()%1024) != 0) packetSize++;
            int pack = packetSize / amountHost;
            if((packetSize%amountHost) != 0) pack++;
            byte[] bytesTab = new byte[1024];
            fileInputStream.seek(numb*pack*1024);

            byte[] bytes = stringToByte(Integer.toString(pack));
            outputStream.write(bytes);
            System.out.println("Mój nr: " + numb);

            int count;
            if(((numb+1)*pack) < packetSize ) {
                for (int i = 0; i < pack; i++) {
                    fileInputStream.read(bytesTab);
                    outputStream.write(bytesTab);
                }
            } else {
                while ((count = fileInputStream.read(bytesTab)) > 0) {
                    outputStream.write(bytesTab, 0, count);
                }
            }

            /*byte[] bytesTab = new byte[1149768];
            for (int i = 0; i < 3; i++){
                outputStream.write(bytesTab, i, bytesTab.length);
            }*/
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

}
