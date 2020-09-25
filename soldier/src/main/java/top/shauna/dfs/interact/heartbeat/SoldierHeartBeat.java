package top.shauna.dfs.interact.heartbeat;

import top.shauna.dfs.bean.BlockInfo;
import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.bean.MetaInfo;
import top.shauna.dfs.block.MetaKeeper;
import top.shauna.dfs.config.PubConfig;
import top.shauna.dfs.monitor.StaticDatas;
import top.shauna.dfs.monitor.bean.StaticBean;
import top.shauna.dfs.protocol.HeartBeatProtocol;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 14:37
 * @E-Mail z1023778132@icloud.com
 */
public class SoldierHeartBeat {

    public HeartBeatResponseBean sendHeartBeat() throws Exception {
        HeartBeatProtocol heartBeatProtocol = ShaunaRPCHandler.getReferenceProxy(HeartBeatProtocol.class);
        HeartBeatRequestBean heartBeatRequestBean = new HeartBeatRequestBean();
        heartBeatRequestBean.setIp(InetAddress.getLocalHost().getHostAddress());
        heartBeatRequestBean.setPort(PubConfig.getInstance().getPort());
        heartBeatRequestBean.setTimeStamp(System.currentTimeMillis());

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

        return heartBeatProtocol.reportHeartBeat(heartBeatRequestBean);
    }

    private Float getTPS(String filePath) {
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

    private Float getQPS(String filePath) {
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
