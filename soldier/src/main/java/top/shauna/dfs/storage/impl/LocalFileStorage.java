package top.shauna.dfs.storage.impl;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.storage.interfaces.StorageEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.WritableByteChannel;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 14:20
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class LocalFileStorage implements StorageEngine {

    private LocalFileStorage(){}

    private static volatile LocalFileStorage localFileStorage;

    public static LocalFileStorage getInstance(){
        if(localFileStorage==null){
            synchronized (LocalFileStorage.class){
                if(localFileStorage==null){
                    localFileStorage = new LocalFileStorage();
                }
            }
        }
        return localFileStorage;
    }

    @Override
    public void write(String url, byte[] content) throws Exception {
        RandomAccessFile writeFile = null;
        try {
            if (isExits(url)) {
                log.warn("文件"+url+"已存在！！！");
            }
            writeFile = new RandomAccessFile(url, "rw");
            writeFile.write(content);
        } catch (FileNotFoundException e) {
            log.error("文件"+url+"不存在 "+e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("写入文件"+url+"出错 "+e.getMessage());
            throw e;
        }finally {
            if(writeFile!=null)
                writeFile.close();
        }
    }

    @Override
    public byte[] read(String url) throws Exception {
        RandomAccessFile readFile = null;
        try {
            readFile = new RandomAccessFile(url, "rw");
            byte[] bytes = new byte[(int)readFile.length()];
            readFile.read(bytes);
            return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally {
            if(readFile!=null)
                readFile.close();
        }
    }

    @Override
    public byte[] read(File file) throws Exception {
        RandomAccessFile readFile = null;
        try {
            readFile = new RandomAccessFile(file, "rw");
            byte[] bytes = new byte[(int)readFile.length()];
            readFile.read(bytes);
            return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally {
            if(readFile!=null)
                readFile.close();
        }
    }

    @Override
    public void read(String url, WritableByteChannel out) throws Exception {
        RandomAccessFile readFile = null;
        try {
            readFile = new RandomAccessFile(url, "rw");
            readFile.getChannel().transferTo(0,readFile.length(), out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally {
            if(readFile!=null)
                readFile.close();
        }
    }

    @Override
    public void delete(String url) {
        File file = new File(url);
        if(!file.exists()){
            log.warn("文件"+url+"不存在");
        }else{
            if(file.isDirectory()){
                log.error("文件"+url+"是目录");
            }else{
                file.delete();
            }
        }
    }

    @Override
    public void append(String url, byte[] content) throws Exception {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(url, "rw");
            file.seek(file.length());
            file.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }finally {
            if(file!=null)
                file.close();
        }
    }

    @Override
    public boolean isExits(String url) {
        return new File(url).exists();
    }

    public boolean isExits(File file) {
        return file.exists();
    }
}
