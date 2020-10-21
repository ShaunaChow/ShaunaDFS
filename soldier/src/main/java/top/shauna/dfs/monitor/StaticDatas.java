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
    private static CopyOnWriteArrayList<StaticBean> readList = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<StaticBean> writeList = new CopyOnWriteArrayList<>();

    public static void addReadData(StaticBean staticBean){
        readList.add(staticBean);
    }

    public static void addWriteData(StaticBean staticBean){
        writeList.add(staticBean);
    }

    public static CopyOnWriteArrayList<StaticBean> getReadList() {
        return readList;
    }

    public static CopyOnWriteArrayList<StaticBean> getWriteList() {
        return writeList;
    }

    public static void resetReadList(){
        readList.clear();
    }

    public static void resetWriteList(){
        writeList.clear();
    }
}
