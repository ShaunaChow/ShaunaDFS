package top.shauna.dfs.interact.soldier;

import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.interact.MessageUtil;
import top.shauna.dfs.protocol.HeartBeatProtocol;
import top.shauna.dfs.soldiermanager.bean.Block;
import top.shauna.rpc.service.ShaunaRPCHandler;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 14:37
 * @E-Mail z1023778132@icloud.com
 */
public class SoldierHeartBeat {
    private HeartBeatProtocol heartBeatProtocol;
    private static volatile SoldierHeartBeat soldierHeartBeat;

    private SoldierHeartBeat(){
        heartBeatProtocol = ShaunaRPCHandler.getReferenceProxy(HeartBeatProtocol.class);
    }

    public static SoldierHeartBeat getInstance(){
        if (soldierHeartBeat==null){
            synchronized (SoldierHeartBeat.class){
                if (soldierHeartBeat==null){
                    soldierHeartBeat = new SoldierHeartBeat();
                }
            }
        }
        return soldierHeartBeat;
    }

    public void regist() throws Exception {
        HeartBeatRequestBean regist = MessageUtil.getHeartBeatRequestBean();
        HeartBeatResponseBean registResp = heartBeatProtocol.regist(regist);
        MessageUtil.dealWithRegistResponse(registResp);
    }

    public void sendHeartBeat() throws Exception {
        HeartBeatRequestBean heartBeatRequestBean = MessageUtil.getHeartBeatRequestBean();
        HeartBeatResponseBean heartBeatResponseBean = heartBeatProtocol.heartBeat(heartBeatRequestBean);
        MessageUtil.dealWithHeartBeatResponse(heartBeatResponseBean);
    }

    public void reportBlocks(HeartBeatRequestBean heartBeatRequestBean) throws Exception {
        HeartBeatResponseBean heartBeatResponseBean = heartBeatProtocol.reportBlocks(heartBeatRequestBean);
        MessageUtil.dealWithBlockResponse(heartBeatResponseBean);
    }

    public void reportAllBlocks() throws Exception {
        HeartBeatRequestBean requestBean = MessageUtil.getHeartBeatRequestBean();
        requestBean.setBlockInfos(MessageUtil.getBlocks());
        requestBean.setReportFlag(0);
        reportBlocks(requestBean);
    }

    public void reportBlock(Block block) throws Exception {
        HeartBeatRequestBean requestBean = MessageUtil.getHeartBeatRequestBean();
        requestBean.setBlockInfos(MessageUtil.wrapBlock(block));
        requestBean.setReportFlag(1);
        reportBlocks(requestBean);
    }
}
