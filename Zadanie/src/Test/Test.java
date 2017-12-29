package Test;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Test extends Thread {

    public void list() {
        try {
            // https://stackoverflow.com/questions/7301764/how-to-get-contents-of-a-folder-and-put-into-an-arraylist
            // tworzenie listy plików znajdujących się w katalogu - ArrayList
            File f = new File("D://TORrent_");
            ArrayList<String> files = new ArrayList<String>(Arrays.asList(f.list()));
            ArrayList<String> listFile = new ArrayList<String>();

            if (files.size() == 0) {
                System.out.println("Katalog jest pusty");
            } else {
                // https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
                // obliczanie sumy kontrolnej dla wszystkich plików w katalogu
                for (int i = 0; i < files.size(); i++) {
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        FileInputStream fis = new FileInputStream("D://TORrent_" + "//" + files.get(i));
                        byte[] dataBytes = new byte[1024];
                        int nread = 0;
                        while ((nread = fis.read(dataBytes)) != -1) {
                            md.update(dataBytes, 0, nread);
                        };
                        fis.close();
                        byte[] mdbytes = md.digest();
                        StringBuffer sb = new StringBuffer();
                        for (int j = 0; j < mdbytes.length; j++) {
                            sb.append(Integer.toString((mdbytes[j] & 0xff) + 0x100, 16).substring(1));
                        }
                        listFile.add((files.get(i) + "\t" + sb.toString()).toString());
                        System.out.println(listFile.get(i));

                    } catch (NoSuchAlgorithmException exc) {
                        System.out.println("Blad: " + exc);
                    } catch (FileNotFoundException exc) {
                        System.out.println("Blad: " + exc);
                    } catch (IOException exc) {
                        System.out.println("Blad: " + exc);
                    }
                }
            }
        } catch (NullPointerException exc) {
            System.out.println("Blad: " + exc);
        }
    }
}
