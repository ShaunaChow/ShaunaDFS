package top.shauna.dfs.interact.soldier.service;

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

    public void reportBlocks(HeartBeatRequestBean heartBeatRequestBean) throws Exception {
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

    public void registerSoldier(HeartBeatRequestBean heartBeatRequestBean) {
        String ip_port = heartBeatRequestBean.getIp() + ":" + heartBeatRequestBean.getPort();
        SoldierInfo tmp = soldierManager.getSoldierInfo(ip_port);
        if (tmp == null) {
            SoldierInfo info = new SoldierInfo(
                    heartBeatRequestBean.getIp(),
                    heartBeatRequestBean.getPort(),
                    true,
                    heartBeatRequestBean.getTimeStamp(),
                    heartBeatRequestBean.getFreeSpace(),
                    heartBeatRequestBean.getBlockInfos(),
                    null,
                    null);
            soldierManager.registSoldier(ip_port, info);
        } else {
            tmp.setOK(true);
            if (tmp.getTimeStamp() < heartBeatRequestBean.getTimeStamp()) {
                tmp.setBlockInfos(heartBeatRequestBean.getBlockInfos());
                tmp.setTimeStamp(heartBeatRequestBean.getTimeStamp());
                tmp.setFreeSpace(heartBeatRequestBean.getFreeSpace());
                soldierManager.adjustSoldierList(tmp);
            }
        }
    }
}

