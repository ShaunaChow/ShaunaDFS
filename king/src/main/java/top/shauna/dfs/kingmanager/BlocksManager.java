package top.shauna.dfs.kingmanager;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 16:33
 * @E-Mail z1023778132@icloud.com
 */
public class BlocksManager implements Starter {
    private static volatile BlocksManager blocksManager;

    private BlocksManager(){
        blocksMap = new ConcurrentHashMap<>();
        soldierManager = SoldierManager.getInstance();
        blockStatus = -1;
    }

    public static BlocksManager getInstance(){
        if(blocksManager==null){
            synchronized (BlocksManager.class){
                if(blocksManager==null){
                    blocksManager = new BlocksManager();
                }
            }
        }
        return blocksManager;
    }

    private ConcurrentHashMap<String,List<Block>> blocksMap;
    private SoldierManager soldierManager;

    public int getBlockStatus() {
        return blockStatus;
    }

    private volatile int blockStatus;

    @Override
    public void onStart() throws Exception {
        CommonThreadPool.threadPool.execute(()->{
            while(blockStatus<0){
                scanBlocks();
                try {
                    TimeUnit.SECONDS.sleep(KingPubConfig.getInstance().getBlockScanTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public List<Block> requireBlocks(String filePath, long fileLength){
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();
        int blockSize = kingPubConfig.getBlockSize();
        int replicas = kingPubConfig.getReplicas();
        int pin = 0;
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<>();
        while(fileLength>0){
            Block block = new Block();
            block.setFilePath(filePath);
            block.setPin(pin++);
            block.setReplicas(replicas);
            block.setBlockLength(fileLength>blockSize?blockSize:(int)fileLength);
            block.setTimeStamp(System.currentTimeMillis());
            List<ReplicasInfo> replicasInfos = soldierManager.getReplicas(replicas);
            block.setReplicasInfos(replicasInfos);
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
        for (int i=0;i<blockList.size();i++) {
            Block block = blockList.get(i);
            block.setPin(i);
            block.setFilePath(filePath);
//            block.setTimeStamp(System.currentTimeMillis());
            int status = -1;
            for (ReplicasInfo replicasInfo : block.getReplicasInfos()) {
                if (replicasInfo.getStatus()!=null&&replicasInfo.getStatus()>=0){
                    status++;
                }
            }
            block.setStatus(status<0?status:(status+1));
        }
        blocksMap.put(filePath,blockList);
    }

    public void deleteBlocks(String filePath){
        blocksMap.remove(filePath);
    }

    private void scanBlocks(){
        double blocksFaultRate = KingPubConfig.getInstance().getBlocksFaultRate();
        int sum=0, ok=0;
        for (List<Block> blocks : blocksMap.values()) {
            for (Block block : blocks) {
                sum++;
                boolean flag = false;
                int okCounter = -1;
                for (ReplicasInfo replicasInfo : block.getReplicasInfos()) {
                    if (replicasInfo.getStatus()!=null){
                        Integer status = replicasInfo.getStatus();
                        status--;
                        if (status>=0){
                            okCounter++;
                            flag = true;
                        }else if (status<-10){
                            /** 此处要做副本处理操作！！！ **/
                        }
                        replicasInfo.setStatus(status);
                    }
                }
                block.setStatus(okCounter);
                if (flag){
                    ok++;
                }
            }
        }
        if (sum==0||(((double)ok/(double)sum)>=blocksFaultRate)) {
            blockStatus = 1;
        }else{
            blockStatus = -1;
        }
    }
}
