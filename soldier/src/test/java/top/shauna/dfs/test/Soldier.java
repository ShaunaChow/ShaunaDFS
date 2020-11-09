package top.shauna.dfs.test;

import org.junit.Test;
import top.shauna.dfs.block.MetaFileScanner;
import top.shauna.dfs.config.SoldierConfig;
import top.shauna.dfs.interact.SoldierInteractStarter;
import top.shauna.dfs.soldierserver.SoldierServerStarter;

/**
 * @Author Shauna.Chow
 * @Date 2020/11/9 15:59
 * @E-Mail z1023778132@icloud.com
 */
public class Soldier {

    @Test
    public void start1() throws Exception {
        System.setProperty("properties","soldier1.properties");

        new SoldierConfig().onStart();

        new MetaFileScanner().onStart();

        new SoldierServerStarter().onStart();

        new SoldierInteractStarter().onStart();

        while(true){}
    }

    @Test
    public void start2() throws Exception {
        System.setProperty("properties","soldier2.properties");

        new SoldierConfig().onStart();

        new MetaFileScanner().onStart();

        new SoldierServerStarter().onStart();

        new SoldierInteractStarter().onStart();

        while(true){}
    }

    @Test
    public void start3() throws Exception {
        System.setProperty("properties","soldier3.properties");

        new SoldierConfig().onStart();

        new MetaFileScanner().onStart();

        new SoldierServerStarter().onStart();

        new SoldierInteractStarter().onStart();

        while(true){}
    }
}
