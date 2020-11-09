package top.shauna.dfs.test;

import org.junit.Test;
import top.shauna.dfs.checkpoint.CheckPointStarter;
import top.shauna.dfs.config.QueenConfig;

/**
 * @Author Shauna.Chow
 * @Date 2020/11/9 15:58
 * @E-Mail z1023778132@icloud.com
 */
public class Queen {

    @Test
    public void start() throws Exception {
        System.setProperty("properties","queen.properties");
        new QueenConfig().onStart();

        new CheckPointStarter().onStart();

        while(true){}
    }
}
