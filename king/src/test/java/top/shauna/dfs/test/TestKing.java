package top.shauna.dfs.test;

import org.junit.Test;
import top.shauna.dfs.interact.heartbeat.KingHeartBeatStarter;

import java.io.IOException;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:46
 * @E-Mail z1023778132@icloud.com
 */
public class TestKing {

    @Test
    public void test5() throws IOException {
        KingHeartBeatStarter kingHeartBeatStarter = new KingHeartBeatStarter();
        kingHeartBeatStarter.onStart();
        System.in.read();
    }
}
