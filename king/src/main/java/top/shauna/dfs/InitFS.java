package top.shauna.dfs;

import top.shauna.dfs.config.KingConfig;
import top.shauna.dfs.kingmanager.LogManager;
import top.shauna.dfs.kingmanager.proxy.ShaunaFSManagerProxy;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 22:10
 * @E-Mail z1023778132@icloud.com
 */
public class InitFS {
    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("properties"));
        new KingConfig().onStart();

        ShaunaFSManagerProxy.getInstance(LogManager.getInstance()).getProxy().initFS();
    }
}
