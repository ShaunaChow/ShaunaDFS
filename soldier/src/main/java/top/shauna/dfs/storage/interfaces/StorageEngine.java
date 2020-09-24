package top.shauna.dfs.storage.interfaces;

import java.nio.channels.WritableByteChannel;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 11:17
 * @E-Mail z1023778132@icloud.com
 */
public interface StorageEngine {
    void write(String url, byte[] content) throws Exception;
    byte[] read(String url) throws Exception;
    void read(String url,WritableByteChannel out) throws Exception;
    void delete(String url);
    void append(String url,byte[] content) throws Exception;
    boolean isExits(String url);
}
