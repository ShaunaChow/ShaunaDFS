package top.shauna.dfs.interact;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 15:04
 * @E-Mail z1023778132@icloud.com
 */
public class MessageUtil {
    private static Long currentTime = 0L;

    public static HeartBeatRequestBean getHeartBeatRequestBean() throws Exception{
        HeartBeatRequestBean heartBeatRequestBean = new HeartBeatRequestBean();
        heartBeatRequestBean.setIp(InetAddress.getLocalHost().getHostAddress());
        heartBeatRequestBean.setPort(SoldierPubConfig.getInstance().getPort());
        long currentTimeMillis = System.currentTimeMillis();
        heartBeatRequestBean.setTimeStamp(currentTimeMillis);
        currentTime = currentTimeMillis;
        long freeSpace = new File(SoldierPubConfig.getInstance().getRootDir()).getFreeSpace();
        heartBeatRequestBean.setFreeSpace(freeSpace);
        return heartBeatRequestBean;
    }

    public static List<BlockInfo> getBlocks() throws Exception{
        ConcurrentHashMap<String, MetaInfo> allBlocks = MetaKeeper.getBlocks();
        List<BlockInfo> blockInfos = new ArrayList<>();
        for (String filePath : allBlocks.keySet()) {
            MetaInfo metaInfo = allBlocks.get(filePath);
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setMetaPath(filePath);
            blockInfo.setFilePath(metaInfo.getFilePath());
            blockInfo.setPin(metaInfo.getPin());
            blockInfo.setTimeStamp(metaInfo.getVersion());
            blockInfo.setQPS(getQPS(filePath));
            blockInfo.setTPS(getTPS(filePath));
            blockInfos.add(blockInfo);
        }
        return blockInfos;
    }

    public static void dealWithResponse(HeartBeatResponseBean heartBeatResponseBean){
        Long timeStamp = heartBeatResponseBean.getTimeStamp();
        if(timeStamp<currentTime){
            return;
        }
        System.out.println(heartBeatResponseBean);
        List<BlockInfo> blockInfos = heartBeatResponseBean.getBlockInfos();
        for (BlockInfo blockInfo : blockInfos) {
            switch (blockInfo.getRes()){
                case SUCCESS: continue;
                case NO_SUCH_BLOCK:
                    MetaInfo metaInfo = MetaKeeper.get(blockInfo.getMetaPath());
                    MetaKeeper.delete(metaInfo.getMetaPath());
                    break;
                case OUT_OF_DATE:

            }
        }
    }

    private static Float getTPS(String filePath) {
        CopyOnWriteArrayList<StaticBean> writeList = StaticDatas.getWriteList(filePath);
        if (writeList==null) return 0f;
        long time = System.currentTimeMillis();
        int nums = 0;
        for(int i=writeList.size()-1;i>=0;i--){
            StaticBean staticBean = writeList.get(i);
            if(staticBean.getStartTime()>=time-10000){
                nums++;
            }else break;
        }
        return nums/10f;
    }

    private static Float getQPS(String filePath) {
        CopyOnWriteArrayList<StaticBean> readList = StaticDatas.getReadList(filePath);
        if (readList==null) return 0f;
        long time = System.currentTimeMillis();
        int nums = 0;
        for(int i=readList.size()-1;i>=0;i--){
            StaticBean staticBean = readList.get(i);
            if(staticBean.getStartTime()>=time-10000){
                nums++;
            }else break;
        }
        return nums/10f;
    }
}
