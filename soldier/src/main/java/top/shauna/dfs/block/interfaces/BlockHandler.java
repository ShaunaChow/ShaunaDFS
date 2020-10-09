package top.shauna.dfs.block.interfaces;

import top.shauna.dfs.soldiermanager.bean.Block;

import java.nio.channels.WritableByteChannel;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 11:08
 * @E-Mail z1023778132@icloud.com
 */
public interface BlockHandler {
    void write(Block block) throws Exception;
    void read(Block block) throws Exception;
    void readAndTransfer(Block block, WritableByteChannel channel) throws Exception;
}
