package top.shauna.dfs.kingmanager;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.kingmanager.bean.SoldierInfo;
import top.shauna.dfs.soldiermanager.bean.BlockInfo;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;
import top.shauna.dfs.type.HeartBeatResponseType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 14:53
 * @E-Mail z1023778132@icloud.com
 */
public class SoldierManager implements Starter {
    private static volatile SoldierManager soldierManager = new SoldierManager();

    private SoldierManager(){
        soldierInfoMap = new ConcurrentHashMap<>();
        header = new SoldierInfo();
        tailer = new SoldierInfo();
        header.next = tailer;
        tailer.pre = header;
        soldiersStatus = -1;
        genId = 0;
    }

    public static SoldierManager getInstance(){
        return soldierManager;
    }

    private ConcurrentHashMap<Integer,SoldierInfo> soldierInfoMap;
    private SoldierInfo header;
    private SoldierInfo tailer;
    private volatile int soldiersStatus;
    private int genId;

    @Override
    public void onStart() throws Exception {
        CommonThreadPool.threadPool.execute(()->{
            while(true){
                try {
                    doScan();
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doScan(){
        Iterator<Integer> iterator = soldierInfoMap.keySet().iterator();
        while(iterator.hasNext()){
            Integer key = iterator.next();
            SoldierInfo soldierInfo = soldierInfoMap.get(key);
            if (soldierInfo.getOK()==null||!soldierInfo.getOK()){
                iterator.remove();
                soldierInfo.pre = soldierInfo.next;
                soldierInfo.next = soldierInfo.pre;
                soldierInfo.next = null;
                soldierInfo.pre = null;
                /**
                 * 转移备份(标记)
                 * **/
                Backup(soldierInfo);
            }
            long cur = System.currentTimeMillis();
            if (cur-soldierInfo.getTimeStamp() >= KingPubConfig.getInstance().getSoldierFaultTime()*1000){
                soldierInfo.setOK(false);
            }
        }
    }

    private void Backup(SoldierInfo soldierInfo) {
        for (BlockInfo blockInfo : soldierInfo.getBlockInfos()) {
            Block block = BlocksManager.getInstance().getBlock(blockInfo.getFilePath(), blockInfo.getPin());
            if(block==null) continue;
            else{
                ReplicasInfo replocasInfo = block.getReplocasInfo(soldierInfo.getId());
                if (replocasInfo==null) continue;
                block.getReplicasInfos().remove(replocasInfo);
                block.setReplicas(block.getReplicas()-1);
            }
        }
    }

    public SoldierInfo getSoldierInfo(Integer id){
        return soldierInfoMap.get(id);
    }

    public void registSoldier(int id,SoldierInfo soldierInfo){
        if(soldierInfoMap.containsKey(id)){
            SoldierInfo info = soldierInfoMap.get(id);
            info.setBlockInfos(soldierInfo.getBlockInfos());
            info.setOK(soldierInfo.getOK());
            info.setTimeStamp(soldierInfo.getTimeStamp());
            info.setFreeSpace(soldierInfo.getFreeSpace());
            info.setIp(soldierInfo.getIp());
            info.setPort(soldierInfo.getPort());
        }else{
            soldierInfoMap.put(id, soldierInfo);
        }
        addIntoList(soldierInfo);
    }

    public void adjustSoldierList(SoldierInfo soldierInfo){
        SoldierInfo pre = soldierInfo.pre;
        SoldierInfo next = soldierInfo.next;
        pre.next = next;
        next.pre = pre;
        while( next!=tailer&&soldierInfo.getPS()>next.getPS() ){
            pre = next;
            next = next.next;
        }
        while( pre!=header&&soldierInfo.getPS()<pre.getPS() ){
            next = pre;
            pre = pre.pre;
        }
        pre.next = soldierInfo;
        soldierInfo.pre = pre;
        soldierInfo.next = next;
        next.pre = soldierInfo;
    }

    private void addIntoList(SoldierInfo soldierInfo){
        if(header.next==tailer) {
            header.next = soldierInfo;
            soldierInfo.pre = header;
            soldierInfo.next = tailer;
            tailer.pre = soldierInfo;
        }else{
            SoldierInfo tmp1 = header;
            SoldierInfo tmp2 = header.next;
            while(tmp2!=tailer&&soldierInfo.getPS()>tmp2.getPS()){
                tmp1 = tmp2;
                tmp2 = tmp2.next;
            }
            tmp1.next = soldierInfo;
            soldierInfo.pre = tmp1;
            soldierInfo.next = tmp2;
            tmp2.pre = soldierInfo;
        }
    }

    public List<ReplicasInfo> getReplicas(int nums, long length){
        if(nums>soldierInfoMap.size()) return null;
        int blockSize = KingPubConfig.getInstance().getBlockSize();
        List<ReplicasInfo> res = new ArrayList<>();
        SoldierInfo soldierInfo = header.next;
        for (int i=0;i<nums;i++){
            if (soldierInfo.getFreeSpace()<length+blockSize*2){
                soldierInfo = soldierInfo.next;
            }
            if (soldierInfo==tailer) return null;
            ReplicasInfo replicasInfo = new ReplicasInfo();
            replicasInfo.setId(soldierInfo.getId());
            replicasInfo.setTimeStamp(System.currentTimeMillis());
            replicasInfo.setStatus(-1);
            replicasInfo.setTPS(0f);
            replicasInfo.setQPS(0f);
            replicasInfo.setIp(soldierInfo.getIp());
            replicasInfo.setPort(soldierInfo.getPort());
            if(i==0) replicasInfo.setMaster(true);
            else replicasInfo.setMaster(false);
            res.add(replicasInfo);
            soldierInfo = soldierInfo.next;
        }
        return res;
    }

    public synchronized int getGenId() {
        return genId++;
    }
}
