package top.shauna.dfs.protocol;

import top.shauna.dfs.bean.Block;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/8 16:27
 * @E-Mail z1023778132@icloud.com
 */
public interface SoldierServerProtocol {
    Block getBlock(Block block);
}
