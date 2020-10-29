package top.shauna.dfs.kingmanager;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.bean.QueenInfo;
import top.shauna.dfs.safemode.SafeModeLock;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/22 17:07
 * @E-Mail z1023778132@icloud.com
 */
public class QueenManager implements Starter {
    private static volatile QueenManager queenManager;
    private ConcurrentHashMap<Integer,QueenInfo> queens;
    private QueenInfo masterQueen;      /** 标记正宫 **/
    private int genId;

    private QueenManager(){
        queens = new ConcurrentHashMap<>();
        genId = 0;
    }

    public static QueenManager getInstance(){
        if (queenManager==null){
            synchronized (QueenManager.class){
                if (queenManager==null){
                    queenManager = new QueenManager();
                }
            }
        }
        return queenManager;
    }

    @Override
    public void onStart() throws Exception {
        CommonThreadPool.threadPool.execute(()->{
            while(true){
                try {
                    TimeUnit.SECONDS.sleep(KingPubConfig.getInstance().getQueenScanTime());
                    doScan();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void doScan() {
        Iterator<Integer> iterator = queens.keySet().iterator();
        while(iterator.hasNext()){
            Integer next = iterator.next();
            QueenInfo queenInfo = queens.get(next);
            long curTime = System.currentTimeMillis();
            if (curTime-queenInfo.getTimeStamp()>KingPubConfig.getInstance().getQueenFaultTime()*1000){
                iterator.remove();
                if (queenInfo==masterQueen){
                    masterQueen = null;
                }
            }
        }
        if (queens.size()==0){      /** 开启保护模式 **/
            SafeModeLock.setQueenOk(false);
            return;
        }else{                      /** 关闭保护模式 **/
            SafeModeLock.setQueenOk(true);
        }
        if (masterQueen==null){     /** 重新选取正宫 **/
            Long freeSpace = 0L;
            for (QueenInfo queenInfo : queens.values()) {
                if (queenInfo.getFreeSpace()>freeSpace){
                    masterQueen = queenInfo;
                    freeSpace = queenInfo.getFreeSpace();
                }
            }
        }
    }

    public QueenInfo getMasterQueen(){
        return masterQueen;
    }

    public void registQueen(QueenInfo queenInfo){
        queens.put(queenInfo.getId(),queenInfo);
        if (masterQueen == null){
            masterQueen = queenInfo;
        }
    }

    public QueenInfo getQueenInfo(int id){
        if (id<0) return null;
        return queens.get(id);
    }

    public int getGenId(){
        return genId++;
    }
}
