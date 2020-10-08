package top.shauna.dfs;

import top.shauna.dfs.block.MetaFileScanner;
import top.shauna.dfs.config.SoldierConfig;
import top.shauna.dfs.interact.SoldierInteractStarter;
import top.shauna.dfs.soldierserver.SoldierServerStarter;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 15:37
 * @E-Mail z1023778132@icloud.com
 */
public class SoldierStarter {
    public static void main(String[] args) throws Exception {
        new SoldierConfig().onStart();

        new MetaFileScanner().onStart();

        new SoldierServerStarter().onStart();

        new SoldierInteractStarter().onStart();
    }
}
