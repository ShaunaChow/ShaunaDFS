package top.shauna.dfs.interact;

import top.shauna.dfs.bean.BlockInfo;
import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.bean.MetaInfo;
import top.shauna.dfs.block.DataKeeper;
import top.shauna.dfs.block.MetaKeeper;
import top.shauna.dfs.config.PubConfig;
import top.shauna.dfs.monitor.StaticDatas;
import top.shauna.dfs.monitor.bean.StaticBean;
import top.shauna.dfs.storage.impl.LocalFileStorage;

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
        heartBeatRequestBean.setPort(PubConfig.getInstance().getPort());
        long currentTimeMillis = System.currentTimeMillis();
        heartBeatRequestBean.setTimeStamp(currentTimeMillis);
        currentTime = currentTimeMillis;

        ConcurrentHashMap<String, MetaInfo> allBlocks = MetaKeeper.getBlocks();
        List<BlockInfo> blockInfos = new ArrayList<>();
        for (String filePath : allBlocks.keySet()) {
            MetaInfo metaInfo = allBlocks.get(filePath);
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setFilePath(filePath);
            blockInfo.setIsOk(true);
            blockInfo.setPin(metaInfo.getPin());
            blockInfo.setVersion(metaInfo.getVersion());
            blockInfo.setQPS(getQPS(filePath));
            blockInfo.setTPS(getTPS(filePath));
            blockInfos.add(blockInfo);
        }

        heartBeatRequestBean.setBlockInfos(blockInfos);
        return heartBeatRequestBean;
    }

    public static void dealWithResponse(HeartBeatResponseBean heartBeatResponseBean){
        Long timeStamp = heartBeatResponseBean.getTimeStamp();
        if(timeStamp<currentTime){
            return;
        }
        List<BlockInfo> blockInfos = heartBeatResponseBean.getBlockInfos();
        for (BlockInfo blockInfo : blockInfos) {
            if (!blockInfo.getIsOk()) {
                deleteFile(blockInfo.getFilePath(),blockInfo.getPin().toString());
            }
        }
    }

    public static void deleteFile(String filePath, String pin) {
        PubConfig pubConfig = PubConfig.getInstance();
        String metaDataDir = pubConfig.getRootDir()+ File.separator+"Meta";
        String metaPath = metaDataDir+  File.separator+
                filePath+"_"+pin+".block";
        MetaInfo metaInfo = MetaKeeper.get(metaPath);
        LocalFileStorage.getInstance().delete(metaInfo.getMetaPath());
        LocalFileStorage.getInstance().delete(metaInfo.getDataInfo().getDataPath());
        MetaKeeper.delete(metaPath);
        DataKeeper.delete(metaInfo.getDataInfo().getMd5());
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
