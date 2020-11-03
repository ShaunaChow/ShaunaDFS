package top.shauna.dfs.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 21:53
 * @E-Mail z1023778132@icloud.com
 */
public interface ClientService {
    boolean uploadFile(String filePath, byte[] data) throws IOException;

    ByteBuffer downloadFile(String filePath);

    boolean mkdir(String dirPath);

    boolean rmFile(String filePath);

    boolean rmDir(String dirPath);
}
