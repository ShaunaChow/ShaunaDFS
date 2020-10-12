package top.shauna.dfs.kingmanager;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.starter.Starter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

    @Override
    public void onStart() throws Exception {

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

    public List<Block> requireBlocks(String filePath){
        return blocksMap.get(filePath);
    }

    public Block getBlock(String filePath, int pin){
        if (!blocksMap.containsKey(filePath)) return null;
        List<Block> blocks = blocksMap.get(filePath);
        if(pin>=blocks.size()) return null;
        return blocks.get(pin);
    }

    public void registBlocks(String filePath, List<Block> blockList){
        for (Block block : blockList) {
            block.setOk(true);
        }
        blocksMap.put(filePath,blockList);
    }
}
