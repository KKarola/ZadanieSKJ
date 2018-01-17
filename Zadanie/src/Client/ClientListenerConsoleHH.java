package Client;

import Config.Config;
import Message.MessageFromConsoleHH;

import java.io.*;

public class ClientListenerConsoleHH extends Thread {
    int sizeOfPacket = Config.INSTANCE.getSizeOfPacket();
    protected InputStream userCommand;
    protected int number;

    public ClientListenerConsoleHH(int number) { this.number = number; }

    public void run() {
        while (true) {
            try {
                userCommand = System.in;
                byte[] byt = new byte[sizeOfPacket];
                userCommand.read(byt);
                String sentence = byteToString(byt);

                MessageFromConsoleHH messageClientConsole = new MessageFromConsoleHH(sentence, number);
                messageClientConsole.answer();

            } catch (IOException e) {
                System.out.println("Error: " + e);
                this.interrupt();
            }
        }

    }

    public String byteToString(byte buffer[]) {
        String date = new String(buffer, 0, buffer.length).trim();
        return date;
    }
}
