package com.bodhi.upd_lib;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author : Sun
 * @version : 1.0
 * create time : 2018/11/16 11:35
 * desc :
 */
public class UPDFileWriter {
    public String filePath;

    private FileDescriptor fd;
    private RandomAccessFile randomAccessFile;
    private BufferedOutputStream out;


    public static UPDFileWriter onBuild(String path) throws IOException {
        File file = new File(path);
        if (file.exists())
            file.delete();

        UPDFileWriter fw = new UPDFileWriter();
        fw.filePath = path;
        fw.randomAccessFile = new RandomAccessFile(new File(path), "rw");
        fw.fd = fw.randomAccessFile.getFD();
        fw.out = new BufferedOutputStream(new FileOutputStream(fw.randomAccessFile.getFD()));
        return fw;
    }

    public void seek(long offset) throws IOException {
        randomAccessFile.seek(offset);
    }

    public void write(byte[] b, int offset, int length) throws IOException {
        out.write(b, offset, length);
    }

    public void flushAndSync() throws IOException {
        out.flush();
        fd.sync();
    }

    public void close() throws IOException {
        out.close();
    }

}
