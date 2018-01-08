package Client;

import Message.MessageFromConsole;

import java.io.*;

public class ClientListenerConsole extends Thread {
    protected InputStream userCommand;
    int number;

    public ClientListenerConsole(int number) {
        this.number = number;
    }

    public void run() {
        while (true) {
            try {
                userCommand = System.in;
                byte[] byt = new byte[1024];
                userCommand.read(byt);
                String sentence = byteToString(byt);

                if(sentence != null) {
                    MessageFromConsole messageClientConsole = new MessageFromConsole(sentence, number);
                    messageClientConsole.answer();
                }

            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }

    }

    public String byteToString(byte buffer[]) {
        String date = new String(buffer, 0, buffer.length).trim();
        return date;
    }
}
