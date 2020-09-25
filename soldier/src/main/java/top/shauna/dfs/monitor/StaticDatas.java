package top.shauna.dfs.monitor;

import top.shauna.dfs.monitor.bean.StaticBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 10:56
 * @E-Mail z1023778132@icloud.com
 */
public class StaticDatas {
    private static ConcurrentHashMap<String,CopyOnWriteArrayList<StaticBean>> readList = new ConcurrentHashMap();
    private static ConcurrentHashMap<String,CopyOnWriteArrayList<StaticBean>> writeList = new ConcurrentHashMap();

    public static void addReadData(StaticBean staticBean){
        String key = staticBean.getBlock().getFilePath();
        if(readList.containsKey(key)){
            readList.get(key).add(staticBean);
        }else{
            CopyOnWriteArrayList<StaticBean> bean = new CopyOnWriteArrayList<>();
            bean.add(staticBean);
            readList.put(key,bean);
        }
    }

    public static void addWriteData(StaticBean staticBean){
        String key = staticBean.getBlock().getFilePath();
        if(writeList.containsKey(key)){
            writeList.get(key).add(staticBean);
        }else{
            CopyOnWriteArrayList<StaticBean> bean = new CopyOnWriteArrayList<>();
            bean.add(staticBean);
            writeList.put(key,bean);
        }
    }

    public static CopyOnWriteArrayList<StaticBean> getReadList(String filePath) {
        return readList.get(filePath);
    }

    public static CopyOnWriteArrayList<StaticBean> getWriteList(String filePath) {
        return writeList.get(filePath);
    }

    public static void restart(){
        readList = new ConcurrentHashMap<>();
        writeList = new ConcurrentHashMap<>();
    }
}
