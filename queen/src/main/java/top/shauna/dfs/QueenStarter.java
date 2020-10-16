package top.shauna.dfs;

import top.shauna.dfs.checkpoint.CheckPointStarter;
import top.shauna.dfs.config.QueenConfig;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 20:25
 * @E-Mail z1023778132@icloud.com
 */
public class QueenStarter {

    public static void main(String[] args) throws Exception {
        new QueenConfig().onStart();

        new CheckPointStarter().onStart();
    }
}
