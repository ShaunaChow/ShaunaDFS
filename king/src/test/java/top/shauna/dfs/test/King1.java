package top.shauna.dfs.test;

import org.junit.Test;
import top.shauna.dfs.config.KingConfig;
import top.shauna.dfs.ha.KingHAStarter;
import top.shauna.dfs.kingmanager.LogManager;
import top.shauna.dfs.kingmanager.proxy.ShaunaFSManagerProxy;
import top.shauna.dfs.threadpool.CommonThreadPool;

/**
 * @Author Shauna.Chow
 * @Date 2020/11/9 15:52
 * @E-Mail z1023778132@icloud.com
 */
public class King1 {

    @Test
    public void start1() throws Exception {
        System.setProperty("properties","king1.properties");

        new KingConfig().onStart();

        new KingHAStarter().onStart();

        while(true){}
    }

    @Test
    public void start2() throws Exception {
        System.setProperty("properties","king2.properties");

        new KingConfig().onStart();

        new KingHAStarter().onStart();

        while(true){}
    }

    @Test
    public void init1() throws Exception {
        System.setProperty("properties","king1.properties");
        new KingConfig().onStart();

        ShaunaFSManagerProxy.getInstance(LogManager.getInstance()).getProxy().initFS();
    }

    @Test
    public void init2() throws Exception {
        System.setProperty("properties","king2.properties");
        new KingConfig().onStart();

        ShaunaFSManagerProxy.getInstance(LogManager.getInstance()).getProxy().initFS();
    }
}
