package Test;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class OpenFile implements Runnable
{
    private FileChannel _channel;
    private FileChannel _writeChannel;
    private int _startLocation;
    private int _size;

    public OpenFile(int loc, int sz, FileChannel chnl, FileChannel write)
    {
        _startLocation = loc;
        _size = sz;
        _channel = chnl;
        _writeChannel = write;
    }

    public void run()
    {
        try
        {
            System.out.println("Reading the channel: " + _startLocation + ":" + _size);
            ByteBuffer buff = ByteBuffer.allocate(_size);
            if (_startLocation == 0)
                Thread.sleep(100);
            _channel.read(buff, _startLocation);
            //ByteBuffer wbuff = ByteBuffer.wrap(buff.array());
            //int written = _writeChannel.write(wbuff, _startLocation);
            //System.out.println("Read the channel: " + buff + ":" + new String(buff.array()) + ":Written:" + written);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
            throws Exception
    {
        FileOutputStream ostr = new FileOutputStream("D://TORrent_1//bvcc1.txt");
        FileInputStream str = new FileInputStream("D://TORrent_1//bvcc.txt");
        String b = "Is this written";
        //ostr.write(b.getBytes());
        FileChannel chnl = str.getChannel();
        FileChannel write = ostr.getChannel();
        ByteBuffer buff = ByteBuffer.wrap(b.getBytes());
        write.write(buff);
        Thread[] th = new Thread[3];
        for (int i = 0; i < 3; i++) {
            th[i] = new Thread(new OpenFile(i*2767, 2767, chnl, write));
            th[i].start();
        }
        /*
        Thread t1 = new Thread(new OpenFile(0, 2767, chnl, write));
        Thread t2 = new Thread(new OpenFile(2767, 2767, chnl, write));
        Thread t3 = new Thread(new OpenFile(5534, 2767, chnl, write));
        t1.start();
        t2.start();
        t3.start();*/
        for (int i = 0; i < 3; i++) {
            th[i].join();
        }
        /*t1.join();
        t2.join();
        t3.join();*/
        write.force(false);
        str.close();
        ostr.close();
    }
}