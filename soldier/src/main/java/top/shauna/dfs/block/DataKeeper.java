package top.shauna.dfs.block;

import top.shauna.dfs.soldiermanager.bean.DataInfo;
import top.shauna.dfs.storage.impl.LocalFileStorage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 17:16
 * @E-Mail z1023778132@icloud.com
 */
public class DataKeeper {
    private static ConcurrentHashMap<String,DataInfo> blocks = new ConcurrentHashMap<>();

    public static void put(String md5,DataInfo path){
        blocks.put(md5,path);
    }

    public static DataInfo get(String md5){
        return blocks.get(md5);
    }

    public static boolean contains(String md5){
        return blocks.containsKey(md5);
    }

    public static void delete(String md5){
        DataInfo dataInfo = blocks.get(md5);
        if (dataInfo==null) return;
        dataInfo.setReference((dataInfo.getReference()==null?0:dataInfo.getReference())-1);
        if (dataInfo.getReference()<=0) {
            blocks.remove(md5);
            LocalFileStorage.getInstance().delete(dataInfo.getDataPath());
        }
        return;
    }
}
