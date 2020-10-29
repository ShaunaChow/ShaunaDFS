package top.shauna.dfs.kingmanager;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.bean.*;
import top.shauna.dfs.safemode.SafeModeLock;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;
import top.shauna.dfs.type.TransactionType;
import top.shauna.dfs.util.KingUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 16:33
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class BlocksManager implements Starter {
    private static volatile BlocksManager blocksManager = new BlocksManager();

    private BlocksManager(){
        blocksMap = new ConcurrentHashMap<>();
        blockStatus = -1;
        backupId = 0;
        backupMap = new HashMap<>();    /** 单线程扫描直接用HashMap **/
    }

    public static BlocksManager getInstance(){
        return blocksManager;
    }

    private ConcurrentHashMap<String,List<Block>> blocksMap;

    public int getBlockStatus() {
        return blockStatus;
    }

    private volatile int blockStatus;
    private int backupId;
    private Map<Block,Integer> backupMap;

    @Override
    public void onStart() throws Exception {
        CommonThreadPool.threadPool.execute(()->{
            while(true){                /** 日常循环，检查坏块哦 **/
                try {
                    TimeUnit.SECONDS.sleep(KingPubConfig.getInstance().getBlockScanTime());
                    scanReplicas();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void scanReplicas() {
        double blocksFaultRate = KingPubConfig.getInstance().getBlocksFaultRate();
        int sum=0, ok=0;
        for (List<Block> blocks : blocksMap.values()) {
            for (Block block : blocks) {
                if (block.getStatus()==2){  /** 新建文件跳过 **/
                    continue;
                }
                sum++;
                Iterator<ReplicasInfo> iterator = block.getReplicasInfos().iterator();
                while (iterator.hasNext()){
                    ReplicasInfo replicasInfo = iterator.next();
                    if (!SoldierManager.getInstance().contains(replicasInfo.getId())){
                        iterator.remove();
                    }
                }
                if (block.getReplicas()>0){
                    block.setStatus(1);
                    ok++;
                    if (block.getReplicas()<KingPubConfig.getInstance().getReplicas()){
                        /**
                         * 转移备份(挂载)
                         * **/
                        if (!SafeModeLock.inSafeMode()) {
                            backup(block);
                        }
                    }else if(block.getReplicas()>KingPubConfig.getInstance().getReplicas()){
                        deleteReplicas(block,KingPubConfig.getInstance().getReplicas());
                        backupMap.remove(block);
                    }else{
                        backupMap.remove(block);
                    }
                }else if (!SafeModeLock.inSafeMode()){  /** 保护模式期间不报失效 **/
                    log.error("Block失效！！！"+block.toString());
                    block.setStatus(-1);
                }
            }
        }
        if (sum==0||(((double)ok/(double)sum)>=blocksFaultRate)) {
            SafeModeLock.setBlockOk(true);
        }else{
            SafeModeLock.setBlockOk(false);
        }
    }

    private void deleteReplicas(Block block, int remain) {
        SoldierManager soldierManager = SoldierManager.getInstance();
        KingUtils.resortReplicas(block);
        List<ReplicasInfo> replicasInfos = block.getReplicasInfos();
        while(block.getReplicas()>remain) {
            ReplicasInfo remove = replicasInfos.remove(replicasInfos.size() - 1);
            block.setReplicas(replicasInfos.size());
            SoldierInfo soldierInfo = soldierManager.getSoldierInfo(remove.getId());
            DeleteBean deleteBean = new DeleteBean(block.getFilePath(),block.getPin());
            soldierInfo.getTransactions().add(new Transaction(getBackupId(),TransactionType.DELETE,deleteBean));
        }
    }

    private void backup(Block block) {
        Integer faultTimes = backupMap.getOrDefault(block, 0);
        if (faultTimes<5){  /** 给出5倍的容错时间 **/
            backupMap.put(block,faultTimes+1);
            return;
        }
        int needReplicas = KingPubConfig.getInstance().getReplicas() - block.getReplicas();
        SoldierManager soldierManager = SoldierManager.getInstance();
        List<ReplicasInfo> newReplicas = soldierManager.getReplicas(KingPubConfig.getInstance().getReplicas(), block.getBlockLength());
        if (newReplicas==null){
            log.error("Soldier备份不足");
            return;
        }
        List<ReplicasInfo> good = block.getReplicasInfos();
        int pin = 0;
        for (int i=0;i<newReplicas.size()&&needReplicas>0;i++) {
            ReplicasInfo newRep = newReplicas.get(i);
            if(isExits(good,newRep)){
                continue;
            }else{
                needReplicas--;
                ReplicasInfo goodReplica = good.get((pin++) % good.size());
                BackupBean backupBean = new BackupBean(block.getFilePath(), block.getPin(), goodReplica, newRep);
                SoldierInfo soldierInfo = soldierManager.getSoldierInfo(newRep.getId());
                soldierInfo.getTransactions().add(new Transaction(getBackupId(),TransactionType.BACK_UP,backupBean));
            }
        }
    }

    private boolean isExits(List<ReplicasInfo> replicas, ReplicasInfo tar){
        for (ReplicasInfo goodReplica : replicas) {
            if (goodReplica.getId()==tar.getId()){
                return true;
            }
        }
        return false;
    }

    public List<Block> requireBlocks(String filePath, long fileLength){
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();
        int blockSize = kingPubConfig.getBlockSize();
        int pin = 0;
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<>();
        while(fileLength>0){
            Block block = new Block();
            block.setFilePath(filePath);
            block.setPin(pin++);
            block.setReplicas(0);
            int length = fileLength > blockSize ? blockSize : (int) fileLength;
            block.setBlockLength(length);
            block.setTimeStamp(System.currentTimeMillis());
            block.setReplicasInfos(new CopyOnWriteArrayList<>());
            blocks.add(block);
            fileLength -= blockSize;
        }
        return blocks;
    }

    public Block getBlock(String filePath, int pin){
        if (!blocksMap.containsKey(filePath)) return null;
        List<Block> blocks = blocksMap.get(filePath);
        if(pin>=blocks.size()) return null;
        return blocks.get(pin);
    }

    public void registBlocks(String filePath, List<Block> blockList){
        blocksMap.put(filePath,blockList);
    }

    public void deleteBlocks(String filePath){
        List<Block> remove = blocksMap.remove(filePath);
        for (Block block : remove) {
            deleteReplicas(block,0);
        }
    }

    public synchronized int getBackupId() {
        return backupId++;
    }
}
