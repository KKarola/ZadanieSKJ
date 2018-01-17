package Client;

import Config.Config;
import Message.MessageFromConsole;

import java.io.*;

public class ClientListenerConsole extends Thread {
    int sizeOfPacket = Config.INSTANCE.getSizeOfPacket();
    protected InputStream userCommand;
    protected int number;

    public ClientListenerConsole(int number) { this.number = number; }

    public void run() {
        while (true) {
            try {
                userCommand = System.in;
                byte[] byt = new byte[sizeOfPacket];
                userCommand.read(byt);
                String sentence = byteToString(byt);

                MessageFromConsole messageClientConsole = new MessageFromConsole(sentence, number);
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
