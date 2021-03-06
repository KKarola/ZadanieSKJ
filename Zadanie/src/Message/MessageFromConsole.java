package Message;

import java.io.*;
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
    protected StringBuffer sb;
    protected boolean fileOK;

    public MessageFromConsole(String sentence, int number) {
        this.sentence = sentence;
        this.number = number;
    }

    public void answer() {
        String[] messageComponents = sentence.split(" ");
        switch (messageComponents[0]) {
            case "LIST":
                sendList();
                break;
            case "PULL":
                pull(messageComponents);
                break;
            case "PUSH":
                push(messageComponents);
                break;
            case "PULL_CONTINUE":
                pull_continue(messageComponents);
                break;
            case "PUSH_CONTINUE":
                push_contiune(messageComponents);
                break;
            case "PULL_MULTI":
                pull_multi(messageComponents);
                break;
            case "EXIT":
                exit();
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

    public void sendList() {
        list();
        for (int i = 0; i < fileTab.length; i++)
            System.out.println(fileTab[i]);
        System.out.println("End of the list.");
    }

    public void checksum(String fileName) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream("D://TORrent_" + number + "//" + fileName);
            byte[] dataBytes = new byte[1024];
            int nread;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
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

    public void check(String[] messageComponents) {
        list();
        fileOK = false;
        checksum(messageComponents[2]);
        for(int i = 0; i < fileTab.length; i++) {
            String[] component = fileTab[i].split(" ");
            if(messageComponents[1].equals(component[0]) && messageComponents[2].equals(component[1]) && sb.toString().equals(component[2]))
                fileOK = true;
        }
        if(fileOK) {
            System.out.println("File transfer completed successfully.");
        } else {
            System.out.println("File transfer was not successfully.");
        }
    }

    public void pull(String[] messageComponents) {
        int port = Integer.parseInt(messageComponents[1]);
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
            byte[] bytes = stringToByte("PULL " + messageComponents[2]);
            outputStream.write(bytes);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            File file = new File("D://TORrent_" + number + "//" + messageComponents[2]);
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

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void push(String[] messageComponents) {
        int port = Integer.parseInt(messageComponents[1]);
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        File file = new File("D://TORrent_" + number + "//" + messageComponents[2]);
        try {
            socket = new Socket("127.0.0.1", PORT + port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            if(file.exists()) {
                checksum(messageComponents[2]);
                byte[] bytes = stringToByte("PUSH " + messageComponents[2] + " " + sb.toString());
                outputStream.write(bytes);

                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytesTab = new byte[1024];
                int count;
                while ((count = fileInputStream.read(bytesTab)) > 0 ) {
                    outputStream.write(bytesTab, 0, count);
                }
                fileInputStream.close();
                socket.shutdownOutput();

                byte[] byt = new byte[1024];
                inputStream.read(byt);
                System.out.println(byteToString(byt));
            } else {
                System.out.println("Check the file name.");
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

    public void pull_continue(String[] messageComponents) {
        int port = Integer.parseInt(messageComponents[1]);
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
            File file = new File("D://TORrent_" + number + "//" + messageComponents[2]);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String fileLen = Long.toString(file.length());
            byte[] bytes = stringToByte("PULL_CONTINUE " + messageComponents[2] + " " + fileLen);
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

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void push_contiune (String[] messageComponents) {
        int port = Integer.parseInt(messageComponents[1]);
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        File file = new File("D://TORrent_" + number + "//" + messageComponents[2]);
        try {
            socket = new Socket("127.0.0.1", PORT + port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            if(file.exists()) {
                checksum(messageComponents[2]);
                byte[] bytes = stringToByte("PUSH_CONTINUE " + messageComponents[2] + " " + sb.toString());
                outputStream.write(bytes);

                byte[] bytesTab = new byte[1024];
                inputStream.read(bytesTab);
                String send = byteToString(bytesTab);
                long fileLen = Long.parseLong(send);

                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.skip(fileLen);
                byte[] byt = new byte[1024];
                int count;
                while ((count = fileInputStream.read(byt)) > 0 ) {
                    outputStream.write(byt, 0, count);
                }
                fileInputStream.close();
                socket.shutdownOutput();

                byte[] bytTab = new byte[1024];
                inputStream.read(bytTab);
                System.out.println(byteToString(bytTab));
            } else {
                System.out.println("Check the file name.");
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

    public void pull_multi(String[] messageComponents) {
        list();
        String[] sentence;
        //tworzymy listę hostów które mogą udostępnić plik
        ArrayList<String> hostList = new ArrayList<>();
        for (int i = 0; i < fileTab.length; i++) {
            sentence = fileTab[i].split(" ");
            if(sentence[1].equals(messageComponents[1]) && sentence[2].equals(messageComponents[2])) hostList.add(sentence[0]);
        }

        try {
            File file = new File("D://TORrent_" + number + "//" + messageComponents[1]);
            RandomAccessFile fileStream = new RandomAccessFile(file, "rw");
            //FileChannel fileChannel = fileStream.getChannel();

            Thread[] multi = new Thread[hostList.size()];
            for (int i = 0; i < hostList.size(); i++) {
                int number = Integer.parseInt(hostList.get(i));
                multi[i] = new Thread(new Multi(fileStream, number, messageComponents[1], hostList.size(), i));
                multi[i].start();
            }

            for (int i = 0; i < hostList.size(); i++) {
                multi[i].join();
            }
            fileStream.close();

            checksum(messageComponents[1]);
            if(sb.toString().equals(messageComponents[2])) {
                System.out.println("File transfer completed successfully.");
            } else {
                System.out.println("File transfer was not successfully.");
            }
        } catch (InterruptedException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void exit() {
        Socket socket = null;
        InputStream inFromServer = null;
        OutputStream outToServer = null;
        try {
            socket = new Socket("127.0.0.1", PORT);
            inFromServer = socket.getInputStream();
            outToServer = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            byte[] bytes = stringToByte("EXIT " + number);
            outToServer.write(bytes);

            byte[] byt = new byte[1024];
            inFromServer.read(byt);
            System.out.println(byteToString(byt));
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
