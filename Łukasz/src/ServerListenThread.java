import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

public class ServerListenThread extends Thread {
    int number;
    protected Vector<String[]> hostVector;
    protected Vector<String> filesAndSum;
    protected ServerSocket serverListenSocket;
    protected Socket client;
    InputStream inGLOBAL;
    OutputStream outGLOBAL;
    protected String[] listOfFiles;
    protected String[] listOfFilesAndMD5;
    protected FileOutputStream out;

    public ServerListenThread(int number, Vector<String[]> hostVector) {
        this.number = number;
        this.hostVector = hostVector;
        this.filesAndSum = new Vector<>();
    }

    public void run() {
        serverListenSocket = null;
        try {
            serverListenSocket = new ServerSocket(10000);
            System.out.println("e");
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            while (true) {
                System.out.println("ECHOECHO");
                System.out.println(this.getState());
                client = serverListenSocket.accept();
                if (!serverListenSocket.isClosed()) {
                    System.out.println("TCP");
                    inGLOBAL = client.getInputStream();
                    outGLOBAL = client.getOutputStream();
                    byte[] bytes = new byte[1024];
                    inGLOBAL.read(bytes);
                    String name = byte2string(bytes);
                    String resp = answer(name, number);
                    // bytes = string2byte(resp);
                    // outGLOBAL.write(bytes);
                    outGLOBAL.close();
                    inGLOBAL.close();
                }
            }
        } catch (IOException e) {
            System.out.println("ServerListenSocketInterrupt! - ServerListenThread run()");
            this.interrupt();
        }

    }

    public String answer(String ask, int n) {
        String answer = "";
        String[] askTab = ask.split(" ");
        if (askTab[0].equals("REGISTER")) {
            if (checkNumber(askTab) == false) {
                String[] hostTab = new String[3];
                hostTab[0] = askTab[1];
                System.out.println(hostTab[0]);
                hostTab[1] = askTab[2];
                System.out.println(hostTab[1]);
                hostTab[2] = askTab[3];
                System.out.println(hostTab[2]);
                hostVector.add(hostTab);
                answer = "Zarejestrowano pomy�lnie";
                try {
                    System.out.println("e");
                    byte[] respBytes = new byte[1024];
                    respBytes = string2byte(answer);
                    outGLOBAL.write(respBytes);
                } catch (IOException e) {
                    System.out.println(e);
                }
            } else if (checkNumber(askTab) == true) {
                answer = "Nie zarejestrowano na serwerze. Zmien numer identyfikacyjny.";
                try {
                    System.out.println("e");
                    byte[] respBytes = new byte[1024];
                    respBytes = string2byte(answer);
                    outGLOBAL.write(respBytes);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }

        else if (askTab[0].equals("UNREGISTER")) {
            System.out.println("UNREGISTERRR");
            for (int i = 0; i < hostVector.size(); i++) {
                System.out.println(askTab[1]);
                if (hostVector.elementAt(i)[0].equals(askTab[1])) {
                    hostVector.removeElementAt(i);
                }
            }
            try {
                outGLOBAL.write(string2byte("UNREGISTERED Success"));
            } catch (IOException e) {
                System.out.println(e);
            }
        } else if (askTab[0].equals("HOSTS")) {
            try {
                int amountOfHosts = hostVector.size();
                byte[] amountOfHostsBytes = new byte[1024];
                byte[] bytes = new byte[1024];
                amountOfHostsBytes = string2byte(String.valueOf(amountOfHosts));
                outGLOBAL.write(amountOfHostsBytes);
                for (int i = 0; i < amountOfHosts; i++) {
                    String resp = hostVector.elementAt(i)[0] + " " + hostVector.elementAt(i)[1] + " "
                            + hostVector.elementAt(i)[2];
                    outGLOBAL.write(string2byte(resp));
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        } else if (askTab[0].equals("FILES")) {
            files();
            try {
                byte[] bytes3 = new byte[1024];
                bytes3 = string2byte(String.valueOf(filesAndSum.size()));
                outGLOBAL.write(bytes3);
                for (int i = 0; i < filesAndSum.size(); i++) {
                    outGLOBAL.write(string2byte(filesAndSum.elementAt(i)));
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return answer;
    }

    public byte[] string2byte(String s) {
        byte[] buff = new byte[1024];
        byte[] b = String.valueOf(s).getBytes();
        for (int i = 0; i < b.length; i++) {
            buff[i] = b[i];
        }
        return buff;
    }

    public String byte2string(byte buff[]) {
        String dane = new String(buff, 0, buff.length).trim();
        return dane;
    }

    public void listOfFiles() {
        try {
            File file = new File("D:\\TORrent_" + number);
            listOfFiles = file.list();
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    public void checkSum() {
        listOfFiles();
        listOfFilesAndMD5 = listOfFiles;
        for (int i = 0; i < listOfFilesAndMD5.length; i++) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                FileInputStream fis = new FileInputStream("D:\\TORrent_" + number + "\\" + listOfFiles[i]);
                byte[] dataBytes = new byte[1024];
                int nread = 0;
                while ((nread = fis.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, nread);
                }
                fis.close();
                byte[] mdbytes = md.digest();
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j < mdbytes.length; j++) {
                    sb.append(Integer.toString((mdbytes[j] & 0xff) + 0x100, 16).substring(1));
                }
                listOfFilesAndMD5[i] = listOfFiles[i] + "\t\t\t" + sb.toString();
            } catch (NoSuchAlgorithmException e) {
                System.out.println(e);
            } catch (FileNotFoundException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void files() {
        int hostAmount = hostVector.size();
        filesAndSum = new Vector<>();
        for (int i = 0; i < hostAmount; i++) {
            Socket socket = null;
            OutputStream out = null;
            InputStream in = null;
            InetAddress inetAddress = null;
            int port = Integer.parseInt(hostVector.elementAt(i)[2]);
            try {
                inetAddress = InetAddress.getLocalHost();
                socket = new Socket(inetAddress.getHostAddress(), port);
                out = socket.getOutputStream();
                in = socket.getInputStream();
            } catch (UnknownHostException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            } catch (IllegalArgumentException e) {
                System.out.println(e);
            }

            try {
                byte[] bytes = string2byte("FILES");
                byte[] bytes2 = new byte[1024];
                out.write(bytes, 0, bytes.length);
                in.read(bytes2);
                String amountOfLines = byte2string(bytes2);
                filesAndSum.addElement(hostVector.elementAt(i)[0] + " " + hostVector.elementAt(i)[1] + " "
                        + hostVector.elementAt(i)[2]);
                for (int j = 0; j < Integer.parseInt(amountOfLines); j++) {
                    in.read(bytes2);
                    String read = byte2string(bytes2);
                    filesAndSum.addElement(read);
                }
                filesAndSum.addElement("");
            } catch (IOException e) {
                System.out.println(e);
                try {
                    serverListenSocket.close();
                } catch (IOException e1) {
                    System.out.println(e);
                }
            } catch (NullPointerException e) {
                System.out.println(e);
            }
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e);
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
    }

    public boolean checkNumber(String[] askTab) {
        boolean checkNumber = false;// true - istnieje numer w wektorze; false -
        // nie istnieje
        for (int i = 0; i < hostVector.size(); i++) {
            if (askTab[1].equals(hostVector.elementAt(i)[0])) {
                checkNumber = true;
            }
        }
        return checkNumber;
    }
}
