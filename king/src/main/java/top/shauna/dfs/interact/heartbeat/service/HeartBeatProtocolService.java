package top.shauna.dfs.interact.heartbeat.service;

import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.kingmanager.BlocksManager;
import top.shauna.dfs.kingmanager.SoldierManager;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.kingmanager.bean.SoldierInfo;
import top.shauna.dfs.soldiermanager.bean.BlockInfo;
import top.shauna.dfs.type.HeartBeatResponseType;

import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 15:53
 * @E-Mail z1023778132@icloud.com
 */
public class HeartBeatProtocolService {
    private SoldierManager soldierManager;
    private BlocksManager blocksManager;

    public HeartBeatProtocolService(){
        soldierManager = SoldierManager.getInstance();
        blocksManager = BlocksManager.getInstance();
    }

    public void reportHeartBeat(HeartBeatRequestBean heartBeatRequestBean) throws Exception {
        String ip_port = heartBeatRequestBean.getIp() + ":" + heartBeatRequestBean.getPort();
        SoldierInfo soldierInfo = soldierManager.getSoldierInfo(ip_port);
        if (soldierInfo == null) {
            SoldierInfo info = new SoldierInfo(
                    heartBeatRequestBean.getIp(),
                    heartBeatRequestBean.getPort(),
                    true,
                    heartBeatRequestBean.getTimeStamp(),
                    heartBeatRequestBean.getBlockInfos(),
                    null,
                    null);
            soldierManager.registSoldier(ip_port, info);
        } else {
            soldierInfo.setOK(true);
            if (soldierInfo.getTimeStamp() < heartBeatRequestBean.getTimeStamp()) {
                soldierInfo.setBlockInfos(heartBeatRequestBean.getBlockInfos());
                soldierManager.adjustSoldierList(soldierInfo);
            }
        }
        List<BlockInfo> blockInfos = heartBeatRequestBean.getBlockInfos();
        for (BlockInfo blockInfo : blockInfos) {
            Block block = blocksManager.getBlock(blockInfo.getFilePath(), blockInfo.getPin());
            if(block==null) blockInfo.setRes(HeartBeatResponseType.NO_SUCH_BLOCK);
            else{
                ReplicasInfo replocasInfo = block.getReplocasInfo(heartBeatRequestBean.getIp(), heartBeatRequestBean.getPort());
                if(replocasInfo==null) blockInfo.setRes(HeartBeatResponseType.NO_SUCH_BLOCK);
                else {
                    if (blockInfo.getTimeStamp()<replocasInfo.getTimeStamp()) {
                        blockInfo.setRes(HeartBeatResponseType.OUT_OF_DATE);
                        ReplicasInfo master = block.getMaster();
                        blockInfo.setMsg("master#"+master.getIp()+":"+master.getPort());
                    }else{
                        replocasInfo.setStatus(1);
                        replocasInfo.setTimeStamp(blockInfo.getTimeStamp());
                        replocasInfo.setQPS(blockInfo.getQPS());
                        replocasInfo.setTPS(blockInfo.getTPS());
                        blockInfo.setRes(HeartBeatResponseType.SUCCESS);
                    }
                }
            }
        }
    }
}

