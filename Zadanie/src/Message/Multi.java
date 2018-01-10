package Message;

import java.io.*;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class Multi extends Thread{
    public static final int PORT = 10000;
    Socket socket = null;
    int number;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    RandomAccessFile fileOutputStream = null;
    FileChannel fileChannel;
    String fileName;
    int amountHost;
    int hostNumber;

    public Multi(RandomAccessFile fileOutputStream, int number, String fileName, int amountHost, int hostNumber, FileChannel fileChanel) {
        this.fileOutputStream = fileOutputStream;
        this.number = number;
        this.fileName = fileName;
        this.amountHost = amountHost;
        this.hostNumber = hostNumber;
        this.fileChannel = fileChanel;
    }

    public void run() {
        synchronized (fileName) {

            socket = null;

            try {
                socket = new Socket("127.0.0.1", PORT + number);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }

            try {
                byte[] bytes = stringToByte("MULTI " + fileName + " " + amountHost + " " + hostNumber);
                outputStream.write(bytes);

                byte[] byt = new byte[1024];
                inputStream.read(byt);
                String sentence = byteToString(byt);
                int pack = Integer.parseInt(sentence);

                byte[] bytesTab = new byte[1024];
                int count;
                FileLock lock = fileChannel.lock();
                fileOutputStream.seek(hostNumber * pack * 1024);
                while ((count = inputStream.read(bytesTab)) > 0) {
                    System.out.println("Teraz działa " + hostNumber + " count: " + count);
                    fileOutputStream.write(bytesTab, 0, count);
                }
                lock.release();
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
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
}
