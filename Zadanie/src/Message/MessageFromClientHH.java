package Message;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageFromClientHH {
    protected String sentence;
    protected Socket socket;
    protected int number;
    protected OutputStream outputStream;
    protected InputStream inputStream;
    protected File file;
    protected ArrayList<String> files;
    protected ArrayList<String> listFile;
    protected StringBuffer sb;
    protected boolean fileOK;

    public MessageFromClientHH(String sentence, Socket socket, int number) {
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
                sendFile(messageComponents);
                break;
            case "PUSH":
                receiveFile(messageComponents);
                break;
            case "PULL_CONTINUE":
                reSendFile(messageComponents);
                break;
            case "PUSH_CONTINUE":
                reReceiveFile(messageComponents);
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

    public void listFile() {
        try{
            file = new File("D://TORrent_" + number);
            files = new ArrayList<>(Arrays.asList(file.list()));
        } catch (NullPointerException e) {
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
            sb = new StringBuffer();
            for (int j = 0; j < mdbytes.length; j++) {
                sb.append(Integer.toString((mdbytes[j] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: " + e);
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void listFileMD5 () {
        listFile = new ArrayList<>();
        listFile();
        for (int i = 0; i < files.size(); i++) {
            checksum(files.get(i));
            listFile.add(files.get(i) + " " + sb.toString());
        }
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

    public void sendFile(String[] messageComponents) {
        try {
            File file = new File("D://TORrent_" + number + "//" + messageComponents[1]);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytesTab = new byte[1024];
                int count;
                while ((count = fileInputStream.read(bytesTab)) > 0 ) {
                    outputStream.write(bytesTab, 0, count);
                }
                outputStream.close();
                fileInputStream.close();
            } else {
                byte[] bytesTab = stringToByte("Check the file name.");
                outputStream.write(bytesTab);
                outputStream.close();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void check(String[] messageComponents){
        try {
            checksum(messageComponents[1]);
            byte[] bytes;
            if(sb.toString().equals(messageComponents[2])) {
                bytes = stringToByte("File transfer completed successfully.");
                fileOK = true;
            } else {
                bytes = stringToByte("File transfer was not successfully.");
                fileOK = false;
            }
            outputStream.write(bytes);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void receiveFile(String[] messageComponents) {
        try {
            File file = new File("D://TORrent_" + number + "//" + messageComponents[1]);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bytesTab = new byte[1024];
            int count;
            while ((count = inputStream.read(bytesTab)) > 0) {
                fileOutputStream.write(bytesTab, 0, count);
            }
            fileOutputStream.close();
            check(messageComponents);
            if (!fileOK) file.delete();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void reSendFile(String[] messageComponents){
        try {
            File file = new File("D://TORrent_" + number + "//" + messageComponents[1]);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                long send = Long.parseLong(messageComponents[2]);
                fileInputStream.skip(send);
                byte[] bytesTab = new byte[1024];
                int count;
                while ((count = fileInputStream.read(bytesTab)) > 0 ) {
                    outputStream.write(bytesTab, 0, count);
                }
                outputStream.close();
                fileInputStream.close();
            } else {
                byte[] bytesTab = stringToByte("Check the file name.");
                outputStream.write(bytesTab);
                outputStream.close();
            }

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void reReceiveFile (String[] messageComponents) {
        try {
            File file = new File("D://TORrent_" + number + "//" + messageComponents[1]);
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
            check(messageComponents);
            if (!fileOK) file.delete();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

}
