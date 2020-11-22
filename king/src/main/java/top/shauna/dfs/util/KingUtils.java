package top.shauna.dfs.util;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.LogManager;
import top.shauna.dfs.kingmanager.SoldierManager;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.SoldierInfo;

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
            SoldierInfo soldierInfo1 = SoldierManager.getInstance().getSoldierInfo(r1.getId());
            if (soldierInfo1==null){
                return 1;
            }
            SoldierInfo soldierInfo2 = SoldierManager.getInstance().getSoldierInfo(r2.getId());
            if (soldierInfo2==null){
                return -1;
            }
            Float ps1 = soldierInfo1.getPS();
            Float ps2 = soldierInfo2.getPS();
            if (ps1==ps2) {
                return 0;
            }else if(ps1>ps2){
                return 1;
            }else{
                return -1;
            }
        });
    }

    /**
     * 注意！！！！
     *      在Windows下文件File.delete()方法不能删除存在流关联的文件
     *      而在Linux下是可以直接删除的！！！这里我们要处理一下
     * **/
    public static void deleteLogs() throws IOException {
//        LogManager.getInstance().getEditLogSystem().changeFile();
        List<File> files = CommonUtil.scanEditLogFiles(KingPubConfig.getInstance().getEditLogDirs());
        for (File file : files) {
            file.delete();
        }
        LogManager.getInstance().getEditLogSystem().changeFile();//先删除后changeFile！！！！
    }
}
