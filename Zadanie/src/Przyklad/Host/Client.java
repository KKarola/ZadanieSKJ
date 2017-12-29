package Przyklad.Host;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;

        Socket clientSocket = new Socket("127.0.0.1", 6789);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        BufferedReader userCommand = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            // wysłanie komunikatu z udostępnionym plikiem: "FILE C://Zdjecia//dupa.jpg"
            // wysłanie komunikatu do pobrania listy plików: "GET"
            sentence = userCommand.readLine();

            outToServer.writeBytes(sentence + '\n');
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
        }
    }
}
