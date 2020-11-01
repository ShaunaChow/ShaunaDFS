package top.shauna.dfs.kingmanager;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.kingmanager.bean.SoldierInfo;
import top.shauna.dfs.soldiermanager.bean.BlockInfo;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;

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
@Slf4j
public class SoldierManager implements Starter {
    private static volatile SoldierManager soldierManager = new SoldierManager();

    private SoldierManager(){
        soldierInfoMap = new ConcurrentHashMap<>();
        header = new SoldierInfo();
        tailer = new SoldierInfo();
        oldHeader = new SoldierInfo();
        header.next = tailer;
        tailer.pre = header;
        soldiersStatus = -1;
        genId = 0;
        MinSpace = KingPubConfig.getInstance().getBlockSize()==null?25165824:KingPubConfig.getInstance().getBlockSize()*2;
    }

    public static SoldierManager getInstance(){
        return soldierManager;
    }

    private ConcurrentHashMap<Integer,SoldierInfo> soldierInfoMap;
    private SoldierInfo header;     /** 工作代链表头 **/
    private SoldierInfo tailer;     /** 工作代链表尾 **/
    private SoldierInfo oldHeader;  /** 养老代链表，不再做分配处理 **/
    private volatile int soldiersStatus;
    private int genId;
    private Integer MinSpace;

    @Override
    public void onStart() throws Exception {
        CommonThreadPool.threadPool.execute(()->{
            while(true){
                try {
                    TimeUnit.SECONDS.sleep(KingPubConfig.getInstance().getSoldierScanTime());
                    doScan();
                    log.info("Soldiers扫描完成！！！");
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
                soldierInfo.pre.next = soldierInfo.next;
                soldierInfo.next.pre = soldierInfo.pre;
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
            if (soldierInfo.getStatus()>0&&soldierInfo.getFreeSpace()<MinSpace){
                moveToOldGen(soldierInfo);
                soldierInfo.setStatus(-1);
            }
            if (soldierInfo.getStatus()<0&&soldierInfo.getFreeSpace()>MinSpace){
                moveToWorkGen(soldierInfo);
                soldierInfo.setStatus(1);
            }
        }
    }

    private void moveToWorkGen(SoldierInfo soldierInfo) {
        /** 从链表中取下来 **/
        soldierInfo.pre.next = soldierInfo.next;
        soldierInfo.next.pre = soldierInfo.pre;
        /** 加入到工作代链表,需要排序 **/
        soldierInfo.next = header.next;
        soldierInfo.pre = header;
        if(soldierInfo.next!=null) {
            soldierInfo.next.pre = soldierInfo;
        }
        header.next = soldierInfo;
        adjustSoldierList(soldierInfo);
    }

    private void moveToOldGen(SoldierInfo soldierInfo){
        /** 从链表中取下来 **/
        soldierInfo.pre.next = soldierInfo.next;
        soldierInfo.next.pre = soldierInfo.pre;
        /** 加入到养老代链表,无需排序！！！ **/
        soldierInfo.next = oldHeader.next;
        soldierInfo.pre = oldHeader;
        if(soldierInfo.next!=null) {
            soldierInfo.next.pre = soldierInfo;
        }
        oldHeader.next = soldierInfo;
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
        if (id==null||id<0) return null;
        return soldierInfoMap.get(id);
    }

    public boolean contains(int id){
        return soldierInfoMap.containsKey(id);
    }

    public void registSoldier(int id, SoldierInfo soldierInfo){
        if(soldierInfoMap.containsKey(id)){
            SoldierInfo info = soldierInfoMap.get(id);
            info.setBlockInfos(soldierInfo.getBlockInfos());
            info.setOK(soldierInfo.getOK());
            info.setTimeStamp(soldierInfo.getTimeStamp());
            info.setFreeSpace(soldierInfo.getFreeSpace());
            info.setIp(soldierInfo.getIp());
            info.setPort(soldierInfo.getPort());
            info.setTPS(soldierInfo.getTPS());
            info.setQPS(soldierInfo.getQPS());
            info.setStatus(soldierInfo.getStatus());
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
        if(nums > soldierInfoMap.size()) return null;
        List<ReplicasInfo> res = new ArrayList<>();
        SoldierInfo soldierInfo = header.next;
        int scanAgain = 1;
        for (int i=0;i<nums;i++){
            if (soldierInfo==tailer) {  /** 给一次重刷的机会 **/
                if (scanAgain>0){
                    soldierInfo = header.next;
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    return null;
                }
            }
            if (soldierInfo.getFreeSpace()<length+MinSpace){
                SoldierInfo tmp = soldierInfo;
                soldierInfo = soldierInfo.next;
                moveToOldGen(tmp);
            }
            if (System.currentTimeMillis() - soldierInfo.getLastUsedTime()<5000){   /** 避免Soldier上线瞬间压力暴增 **/
                soldierInfo = soldierInfo.next;
                if (soldierInfo==tailer) {
                    continue;
                }
            }
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
            soldierInfo.setLastUsedTime(System.currentTimeMillis());
            soldierInfo = soldierInfo.next;
        }
        return res;
    }

    public synchronized int getGenId() {
        return genId++;
    }

    public ConcurrentHashMap<Integer, SoldierInfo> getSoldierInfoMap() {
        return soldierInfoMap;
    }
}
