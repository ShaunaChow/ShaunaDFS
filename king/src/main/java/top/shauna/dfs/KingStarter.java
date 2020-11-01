package top.shauna.dfs;

import top.shauna.dfs.config.KingConfig;
import top.shauna.dfs.ha.KingHAStarter;
import top.shauna.dfs.interact.client.ClientProtocolStarter;
import top.shauna.dfs.interact.soldier.KingHeartBeatStarter;
import top.shauna.dfs.kingmanager.ManagerStarter;
import top.shauna.dfs.monitor.MonitorStarter;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 20:42
 * @E-Mail z1023778132@icloud.com
 */
public class KingStarter {
    public static void main(String[] args) throws Exception {
        new KingConfig().onStart();

        new KingHAStarter().onStart();

        new MonitorStarter().onStart();
    }
}
