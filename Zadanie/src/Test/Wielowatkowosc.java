package Test;
/*
import sun.misc.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Wielowatkowosc {
    public static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException, IOException {
        File f = new File("test.txt");
        RandomAccessFile file = new RandomAccessFile("test.txt", "rw");
        latch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(new WritingThread(i, (long) i * 10, file.getChannel()));
            t.start();

        }
        latch.await();
        file.close();
        InputStream fileR = new InputStream("test.txt");
        byte[] bytes = IOUtils.toByteArray(fileR);
        for (int i = 0; i < bytes.length; i++) {
            System.out.println(bytes[i]);

        }
    }

    public static class WritingThread implements Runnable {
        private long startPosition = 0;
        private FileChannel channel;
        private int id;

        public WritingThread(int id, long startPosition, FileChannel channel) {
            super();
            this.startPosition = startPosition;
            this.channel = channel;
            this.id = id;

        }

        private ByteBuffer generateStaticBytes() {
            ByteBuffer buf = ByteBuffer.allocate(10);
            byte[] b = new byte[10];
            for (int i = 0; i < 10; i++) {
                b[i] = (byte) (this.id * 10 + i);

            }
            buf.put(b);
            buf.flip();
            return buf;

        }

        @Override
        public void run() {
            Random r = new Random();
            while (r.nextInt(100) != 50) {
                try {
                    System.out.println("Thread  " + id + " is Writing");
                    this.channel.write(this.generateStaticBytes(), this.startPosition);
                    this.startPosition += 10;
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            latch.countDown();
        }
    }
}*/