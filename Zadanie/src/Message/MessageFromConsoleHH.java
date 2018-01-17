package Message;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageFromConsoleHH {
    protected String sentence;
    protected int number;
    protected String[] fileTab;
    protected StringBuffer sb;
    protected int port;
    protected boolean fileOK;

    public MessageFromConsoleHH(String sentence, int number) {
        this.sentence = sentence;
        this.number = number;
        if(this.number == 1) port = 10002;
        if(this.number == 2) port = 10001;
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
            socket = new Socket("127.0.0.1", port);
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
        checksum(messageComponents[1]);
        for(int i = 0; i < fileTab.length; i++) {
            String[] component = fileTab[i].split(" ");
            if(sb.toString().equals(component[1]))
                fileOK = true;
        }
        if(fileOK) {
            System.out.println("File transfer completed successfully.");
        } else {
            System.out.println("File transfer was not successfully.");
        }
    }

    public void pull(String[] messageComponents) {
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket("127.0.0.1", port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            byte[] bytes = stringToByte("PULL " + messageComponents[1]);
            outputStream.write(bytes);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

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

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void push(String[] messageComponents) {
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        File file = new File("D://TORrent_" + number + "//" + messageComponents[1]);
        try {
            socket = new Socket("127.0.0.1", port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            if (file.exists()){
                checksum(messageComponents[1]);
                byte[] bytes = stringToByte("PUSH " + messageComponents[1] + " " + sb.toString());
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
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket("127.0.0.1", port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            File file = new File("D://TORrent_" + number + "//" + messageComponents[1]);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String fileLen = Long.toString(file.length());
            byte[] bytes = stringToByte("PULL_CONTINUE " + messageComponents[1] + " " + fileLen);
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
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        File file = new File("D://TORrent_" + number + "//" + messageComponents[1]);
        try {
            socket = new Socket("127.0.0.1", port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            if (file.exists()) {
                checksum(messageComponents[1]);
                byte[] bytes = stringToByte("PUSH_CONTINUE " + messageComponents[1] + " " + sb.toString());
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
        }catch (IOException e) {
            System.out.println("Error: " + e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
    }
}
