package top.shauna.dfs.interact.soldier.service;

import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.BlocksManager;
import top.shauna.dfs.kingmanager.SoldierManager;
import top.shauna.dfs.kingmanager.bean.*;
import top.shauna.dfs.soldiermanager.bean.BlockInfo;
import top.shauna.dfs.type.HeartBeatResponseType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
        int id = heartBeatRequestBean.getId();
        List<BlockInfo> blockInfos = heartBeatRequestBean.getBlockInfos();
        List<BlockInfo> newBlockInfos = new CopyOnWriteArrayList<>();
        for (BlockInfo blockInfo : blockInfos) {
            Block block = blocksManager.getBlock(blockInfo.getFilePath(), blockInfo.getPin());
            if(block==null) blockInfo.setRes(HeartBeatResponseType.NO_SUCH_BLOCK);
            else{
                ReplicasInfo replicasInfo = block.getReplocasInfo(heartBeatRequestBean.getId());
                if(replicasInfo==null) {    /** 不存在 **/
                    if(block.getReplicas()>= KingPubConfig.getInstance().getReplicas()){    /** 备份条件满足 不需要创建 **/
                        blockInfo.setRes(HeartBeatResponseType.NO_SUCH_BLOCK);
                    }else{                  /** 创建 **/
                        replicasInfo = new ReplicasInfo();
                        replicasInfo.setId(id);
                        replicasInfo.setStatus(1);
                        replicasInfo.setIp(heartBeatRequestBean.getIp());
                        replicasInfo.setPort(heartBeatRequestBean.getPort());
                        replicasInfo.setTimeStamp(blockInfo.getTimeStamp());
                        block.getReplicasInfos().add(replicasInfo);
                        block.setReplicas(block.getReplicas()+1);
                        blockInfo.setRes(HeartBeatResponseType.SUCCESS);
                        newBlockInfos.add(blockInfo);
                    }
                }else {     /** 更新操作 **/
                    replicasInfo.setId(id);
                    replicasInfo.setStatus(1);
                    replicasInfo.setTimeStamp(blockInfo.getTimeStamp());
                    blockInfo.setRes(HeartBeatResponseType.SUCCESS);
                    newBlockInfos.add(blockInfo);
                }
            }
        }
        SoldierInfo tmp = soldierManager.getSoldierInfo(id);
        if (heartBeatRequestBean.getReportFlag()==null||heartBeatRequestBean.getReportFlag()==0){   /** 整体汇报 **/
            tmp.setBlockInfos(newBlockInfos);
        }else{      /** 增量汇报 **/
            tmp.getBlockInfos().addAll(newBlockInfos);
        }
    }

    public boolean heartBeat(HeartBeatRequestBean heartBeatRequestBean) {
        int id = heartBeatRequestBean.getId();
        SoldierInfo tmp = soldierManager.getSoldierInfo(id);
        if (tmp == null) {
            return false;
        } else {
            tmp.setOK(true);
            if (tmp.getTimeStamp() < heartBeatRequestBean.getTimeStamp()) {
                tmp.setTimeStamp(System.currentTimeMillis());
                tmp.setFreeSpace(heartBeatRequestBean.getFreeSpace());
                tmp.setQPS(heartBeatRequestBean.getQPS());
                tmp.setTPS(heartBeatRequestBean.getTPS());
                ArrayList<Transaction> transactions = new ArrayList<>();
                if (tmp.getTransactions()!=null&&tmp.getTransactions().size()!=0) {
                    transactions.addAll(tmp.getTransactions());
                    tmp.getTransactions().clear();
                }
                heartBeatRequestBean.setTransactions(transactions);
                soldierManager.adjustSoldierList(tmp);
            }
            return true;
        }
    }

    public void regist(HeartBeatRequestBean heartBeatRequestBean) {
        int id = soldierManager.getGenId();
        SoldierInfo info = new SoldierInfo(
                id,
                heartBeatRequestBean.getIp(),
                heartBeatRequestBean.getPort(),
                true,
                System.currentTimeMillis(),
                heartBeatRequestBean.getFreeSpace(),
                new CopyOnWriteArrayList<>(),
                new CopyOnWriteArrayList<>(),
                heartBeatRequestBean.getQPS()==null?0F:heartBeatRequestBean.getQPS(),
                heartBeatRequestBean.getTPS()==null?0F:heartBeatRequestBean.getTPS(),
                0L,
                1,
                null,
                null);
        soldierManager.registSoldier(id, info);
        heartBeatRequestBean.setId(id);
    }
}

