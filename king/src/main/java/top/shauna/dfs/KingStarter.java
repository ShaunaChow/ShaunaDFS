package top.shauna.dfs;

import top.shauna.dfs.config.KingConfig;
import top.shauna.dfs.interact.client.ClientProtocolStarter;
import top.shauna.dfs.interact.heartbeat.KingHeartBeatStarter;
import top.shauna.dfs.kingmanager.ShaunaFSManager;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 20:42
 * @E-Mail z1023778132@icloud.com
 */
public class KingStarter {
    public static void main(String[] args) throws Exception {
        new KingConfig().onStart();

        ShaunaFSManager.getInstance().onStart();

        new KingHeartBeatStarter().onStart();

        new ClientProtocolStarter().onStart();
    }
}
