package top.shauna.dfs.util;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.LogManager;
import top.shauna.dfs.kingmanager.SoldierManager;
import top.shauna.dfs.kingmanager.bean.Block;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/28 21:04
 * @E-Mail z1023778132@icloud.com
 */
public class KingUtils {

    public static void resortReplicas(Block block){
        block.getReplicasInfos().sort((r1,r2)->{
            Float ps1 = SoldierManager.getInstance().getSoldierInfo(r1.getId()).getPS();
            Float ps2 = SoldierManager.getInstance().getSoldierInfo(r2.getId()).getPS();
            if (ps1==ps2) {
                return 0;
            }else if(ps1>ps2){
                return 1;
            }else{
                return -1;
            }
        });
    }

    public static void deleteLogs() throws IOException {
        LogManager.getInstance().getEditLogSystem().changeFile();
        List<File> files = CommonUtil.scanEditLogFiles(KingPubConfig.getInstance().getEditLogDirs());
        for (File file : files) {
            file.delete();
        }
    }
}
