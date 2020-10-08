package top.shauna.dfs.soldierserver;

import top.shauna.dfs.starter.Starter;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/8 16:45
 * @E-Mail z1023778132@icloud.com
 */
public class SoldierServerStarter implements Starter {
    @Override
    public void onStart() throws Exception {
        SoldierServerUtil.startServer();
    }
}
