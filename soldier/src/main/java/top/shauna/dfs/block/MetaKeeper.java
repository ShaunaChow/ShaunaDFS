package top.shauna.dfs.block;

import top.shauna.dfs.soldiermanager.bean.DataInfo;
import top.shauna.dfs.soldiermanager.bean.MetaInfo;
import top.shauna.dfs.storage.impl.LocalFileStorage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 20:45
 * @E-Mail z1023778132@icloud.com
 */
public class MetaKeeper {
    private static ConcurrentHashMap<String,MetaInfo> blocks = new ConcurrentHashMap<>();

    public static void put(String metaPath,MetaInfo path){
        blocks.put(metaPath,path);
    }

    public static MetaInfo get(String metaPath){
        return blocks.get(metaPath);
    }

    public static boolean contains(String metaPath){
        return blocks.containsKey(metaPath);
    }

    public static ConcurrentHashMap<String, MetaInfo> getBlocks() {
        return blocks;
    }

    public static void delete(String metaPath){
        MetaInfo metaInfo = blocks.get(metaPath);
        LocalFileStorage.getInstance().delete(metaPath);
        DataInfo dataInfo = metaInfo.getDataInfo();
        DataKeeper.delete(dataInfo.getMd5());
        blocks.remove(metaPath);
    }
}
