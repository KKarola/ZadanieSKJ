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

public class ClientListenThreadH2H extends Thread {
    int number;
    protected ServerSocket clientListenSocket;
    protected Socket client;
    InputStream inGLOBAL;
    OutputStream outGLOBAL;
    protected String[] listOfFiles;
    protected String[] listOfFilesAndMD5;
    protected FileOutputStream out;

    public ClientListenThreadH2H(int number) {
        this.number = number;
    }

    public void run() {
        clientListenSocket = null;
        try {
            clientListenSocket = new ServerSocket(10000 + number);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            while (true) {
                client = clientListenSocket.accept();
                if (!clientListenSocket.isClosed()) {
                    inGLOBAL = client.getInputStream();
                    outGLOBAL = client.getOutputStream();
                    byte[] bytes = new byte[1024];
                    inGLOBAL.read(bytes);
                    String name = byte2string(bytes);
                    String resp = answer(name, number);
                    outGLOBAL.close();
                    inGLOBAL.close();
                }
                else{
                    System.out.println("e");
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            this.interrupt();
        }
    }

    public String answer(String ask, int n) {
        String answer = "";
        String[] askTab = ask.split(" ");
        if (askTab[0].equals("ECHO")) {
            try {
                for (int i = 3; i < askTab.length; i++) {
                    answer = answer + askTab[i] + " ";
                }
                byte[] respBytes = new byte[1024];
                respBytes = string2byte(answer);
                outGLOBAL.write(respBytes);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        else if (askTab[0].equals("PUSH")) {
            try {
                String fileName=fileName(askTab);
                out = new FileOutputStream("D:\\TORrent_" + n + "\\" + fileName);
                byte[] bytes2 = new byte[1024];
                int count;
                int packageNumber = 1;
                while ((count = inGLOBAL.read(bytes2)) > 0) {
                    out.write(bytes2, 0, count);
                }
                out.close();
                answer = "Zakonczono transfer pliku";
                byte[] respBytes = new byte[1024];
                respBytes = string2byte(answer);
                outGLOBAL.write(respBytes);
            } catch (FileNotFoundException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);

                try {
                    out.close();
                } catch (IOException e1) {
                    System.out.println(e);
                }
            }

        } else if (askTab[0].equals("PUSHRESUME")) {
            try {
                String fileName=fileName(askTab);
                File file = new File("D:\\TORrent_" + n + "\\" + fileName);
                //
                long length = file.length();
                int iloscPaczek = (int) (length / 1024);
                String iloscPaczekString = String.valueOf(iloscPaczek);
                System.out.println("e");
                out = new FileOutputStream("D:\\TORrent_" + n + "\\" + askTab[3], true);
                byte[] bytes2 = new byte[1024];
                int count;
                outGLOBAL.write(string2byte(iloscPaczekString));
                int packageNumber = iloscPaczek;
                while ((count = inGLOBAL.read(bytes2)) > 0) {
                    out.write(bytes2, 0, count);
                }
                out.close();
                answer = "Zakonczono transfer pliku";
                byte[] respBytes = new byte[1024];
                respBytes = string2byte(answer);
                outGLOBAL.write(respBytes);
            } catch (FileNotFoundException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        else if (askTab[0].equals("PULL")) {
            try {
                String fileName=fileName(askTab);
                File file = new File("D:\\TORrent_" + n + "\\" + fileName);
                //
                FileInputStream in = new FileInputStream(file);
                byte[] bytes2 = new byte[1024];
                int count;
                while ((count = in.read(bytes2)) > 0) {
                    outGLOBAL.write(bytes2, 0, count);
                }
                in.close();
                answer = "Zakonczono transfer pliku";
            } catch (FileNotFoundException e) {
                try {
                    System.out.println(e);
                    outGLOBAL.close();
                } catch (IOException e1) {
                    System.out.println(e);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        } else if (askTab[0].equals("PULLRESUME")) {
            try {
                String fileName=fileName(askTab);
                File file = new File("D:\\TORrent_" + n + "\\" + fileName);
                //
                FileInputStream in = new FileInputStream(file);
                byte[] packageAmountBytes = new byte[1024];

                inGLOBAL.read(packageAmountBytes);
                String packageAmountString = byte2string(packageAmountBytes);
                int packageAmountInt = Integer.parseInt(packageAmountString);
                System.out.println("e");
                byte[] bytes2 = new byte[1024];
                for (int i = 0; i < packageAmountInt; i++) {
                    in.read(bytes2);
                }
                int count;
                while ((count = in.read(bytes2)) > 0) {
                    outGLOBAL.write(bytes2, 0, count);
                }
                in.close();
                answer = "Zakonczono transfer pliku";
            } catch (FileNotFoundException e) {
                try {
                    System.out.println(e);
                    outGLOBAL.close();
                } catch (IOException e1) {
                    System.out.println(e);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        else if (askTab[0].equals("LIST")) {
            checkSum();
            for (int i = 0; i < listOfFilesAndMD5.length; i++) {
                try {
                    byte[] bytes = new byte[1024];
                    bytes = string2byte(listOfFilesAndMD5[i]);
                    outGLOBAL.write(bytes, 0, bytes.length);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            try {
                answer = "Koniec Listy";
                byte[] respBytes = new byte[1024];
                respBytes = string2byte(answer);
                outGLOBAL.write(respBytes);
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

    public String fileName(String[] nameTab){
        String fileName=nameTab[3];
        if (nameTab.length>4) {
            for (int i = 4; i < nameTab.length; i++) {
                fileName=fileName+" "+nameTab[i];
            }
        }
        return fileName;
    }
}
