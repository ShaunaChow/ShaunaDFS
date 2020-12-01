package top.shauna.dfs.interact;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.interact.soldier.SoldierHeartBeat;
import top.shauna.dfs.kingmanager.bean.Transaction;
import top.shauna.dfs.soldiermanager.bean.Block;
import top.shauna.dfs.soldiermanager.bean.BlockInfo;
import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.soldiermanager.bean.MetaInfo;
import top.shauna.dfs.block.MetaKeeper;
import top.shauna.dfs.config.SoldierPubConfig;
import top.shauna.dfs.monitor.StaticDatas;
import top.shauna.dfs.monitor.bean.StaticBean;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 15:04
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class MessageUtil {
    private static Long currentTime = 0L;
    private static int idKeeper = -99999999;
    private static BlockQueue undoneTrasactions;

    static {
        undoneTrasactions = new BlockQueue();
    }

    public static HeartBeatRequestBean getHeartBeatRequestBean() throws Exception{
        HeartBeatRequestBean heartBeatRequestBean = new HeartBeatRequestBean();
        SoldierPubConfig soldierPubConfig = SoldierPubConfig.getInstance();
        heartBeatRequestBean.setId(getIdKeeper());
        heartBeatRequestBean.setIp(soldierPubConfig.getExportIP());
        heartBeatRequestBean.setPort(soldierPubConfig.getPort());
        long currentTimeMillis = System.currentTimeMillis();
        heartBeatRequestBean.setTimeStamp(currentTimeMillis);
        currentTime = currentTimeMillis;
        long freeSpace = new File(soldierPubConfig.getRootDir()).getFreeSpace();
        heartBeatRequestBean.setFreeSpace(freeSpace);
        heartBeatRequestBean.setTPS(getTPS());
        heartBeatRequestBean.setQPS(getQPS());
        return heartBeatRequestBean;
    }

    public static List<BlockInfo> getBlocks(){
        ConcurrentHashMap<String, MetaInfo> allBlocks = MetaKeeper.getBlocks();
        List<BlockInfo> blockInfos = new ArrayList<>();
        for (String filePath : allBlocks.keySet()) {
            MetaInfo metaInfo = allBlocks.get(filePath);
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setMetaPath(filePath);
            blockInfo.setFilePath(metaInfo.getFilePath());
            blockInfo.setPin(metaInfo.getPin());
            blockInfo.setTimeStamp(metaInfo.getVersion());
            blockInfos.add(blockInfo);
        }
        return blockInfos;
    }

    public static List<BlockInfo> wrapBlock(Block block){
        ArrayList<BlockInfo> res = new ArrayList<>(1);
        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setFilePath(block.getFilePath());
        blockInfo.setMetaPath(
                SoldierPubConfig.getInstance().getRootDir()+ File.separator+"Meta"+block.getFilePath()+"_"+block.getPin()+".block"
        );
        blockInfo.setPin(block.getPin());
        blockInfo.setTimeStamp(block.getVersion());
        res.add(blockInfo);
        return res;
    }

    public static void dealWithRegistResponse(HeartBeatResponseBean heartBeatResponseBean) throws Exception {
        if (heartBeatResponseBean.getRes()==null){
            log.error("RES为null！！！");
            throw new Exception("注测出错");
        }
        switch (heartBeatResponseBean.getRes()){
            case SUCCESS:
                setIdKeeper(heartBeatResponseBean.getId());
                log.info("注测ok");
                break;
            case UNKNOWN:
                log.error("未知错误");
                break;
        }
    }

    public static void dealWithBlockResponse(HeartBeatResponseBean heartBeatResponseBean) throws Exception {
        if (heartBeatResponseBean.getRes()==null){
            log.error("RES为null！！！");
            throw new Exception("汇报Block出错");
        }
        switch (heartBeatResponseBean.getRes()){
            case SUCCESS:
                List<BlockInfo> blockInfos = heartBeatResponseBean.getBlockInfos();
                for (BlockInfo blockInfo : blockInfos) {
                    dealWithBlockInfoRes(blockInfo);
                }
                break;
            case REPORT_BLOCKS_AGAIN:
                SoldierHeartBeat soldierHeartBeat = SoldierHeartBeat.getInstance();
                soldierHeartBeat.regist();
            case UNKNOWN:
                log.error("未知错误");
                break;
        }
    }

    public static void dealWithHeartBeatResponse(HeartBeatResponseBean heartBeatResponseBean) throws Exception {
        switch (heartBeatResponseBean.getRes()){
            case SUCCESS:
                List<Transaction> transactions = heartBeatResponseBean.getTransactions();
                if(transactions!=null) {
                    undoneTrasactions.addAll(transactions);
                }
                break;
            case UNKNOWN:
                log.error("未知错误！！！");
                break;
            case REPORT_BLOCKS_AGAIN:
                SoldierHeartBeat soldierHeartBeat = SoldierHeartBeat.getInstance();
                soldierHeartBeat.regist();
                soldierHeartBeat.reportAllBlocks();
                break;
        }
    }

    private static void dealWithBlockInfoRes(BlockInfo blockInfo){
        switch (blockInfo.getRes()){
            case SUCCESS: break;
            case NO_SUCH_BLOCK:
                MetaInfo metaInfo = MetaKeeper.get(blockInfo.getMetaPath());
                MetaKeeper.delete(metaInfo.getMetaPath());
                break;
            case OUT_OF_DATE:

        }
    }

    private static float getTPS() {
        CopyOnWriteArrayList<StaticBean> writeList = StaticDatas.getWriteList();
        if (writeList==null) return 0f;
        int size = writeList.size();
        StaticDatas.resetWriteList();
        return size/(float)(SoldierPubConfig.getInstance().getHeartBeatTime());
    }

    private static float getQPS() {
        CopyOnWriteArrayList<StaticBean> readList = StaticDatas.getReadList();
        if (readList==null) return 0f;
        int size = readList.size();
        StaticDatas.resetReadList();
        return size/(float)(SoldierPubConfig.getInstance().getHeartBeatTime());
    }

    public synchronized static int getIdKeeper() {
        return idKeeper;
    }

    public synchronized static void setIdKeeper(int idKeeper) {
        MessageUtil.idKeeper = idKeeper;
    }

    public static BlockQueue getUndoneTrasactions() {
        return undoneTrasactions;
    }
}
