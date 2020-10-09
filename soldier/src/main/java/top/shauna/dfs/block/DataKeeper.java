package top.shauna.dfs.block;

import top.shauna.dfs.soldiermanager.bean.DataInfo;

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

    public static DataInfo delete(String md5){
        return blocks.remove(md5);
    }
}
