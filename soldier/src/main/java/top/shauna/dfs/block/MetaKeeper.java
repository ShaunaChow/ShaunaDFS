package top.shauna.dfs.block;

import top.shauna.dfs.bean.MetaInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 20:45
 * @E-Mail z1023778132@icloud.com
 */
public class MetaKeeper {
    private static ConcurrentHashMap<String,MetaInfo> blocks = new ConcurrentHashMap<>();

    public static void put(String md5,MetaInfo path){
        blocks.put(md5,path);
    }

    public static MetaInfo get(String md5){
        return blocks.get(md5);
    }

    public static boolean contains(String md5){
        return blocks.containsKey(md5);
    }

    public static ConcurrentHashMap<String, MetaInfo> getBlocks() {
        return blocks;
    }

    public static MetaInfo delete(String md5){
        return blocks.remove(md5);
    }
}
