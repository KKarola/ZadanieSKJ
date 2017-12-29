package Przyklad.Tracker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

class Server {

    public static void main(String argv[]) throws Exception
    {
        // tworzę pustą listę z plikami hostów
        ArrayList fileList = new ArrayList();

        String clientSentence;
        ServerSocket welcomeSocket = new ServerSocket(6789);

        Socket connectionSocket = welcomeSocket.accept();
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

        while(true) {

            clientSentence = inFromClient.readLine();
            System.out.println("Otrzymano od klienta: " + clientSentence);

            // zapisanie odebranego komunikatu jako obiekt Message
            Message message = new Message(clientSentence);

            // tutaj jest logika, jak obsługiwać komunikaty
            switch (message.type) {
                case "FILE":
                    fileList.add(message.param);
                    System.out.println(fileList);
                    outToClient.writeBytes("ADDED "+message.param + '\n');
                    break;
                case "GET":
                    System.out.println("lista plikow");
                    outToClient.writeBytes(fileList.toString() + '\n');
                    break;
                default:
                    outToClient.writeBytes("NOT RECOGNIZED COMMAND" + '\n');
                    break;
            }

        }
    }
}