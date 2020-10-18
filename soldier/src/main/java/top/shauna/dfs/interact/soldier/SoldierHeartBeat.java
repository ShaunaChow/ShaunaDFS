package top.shauna.dfs.interact.soldier;

import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.config.SoldierPubConfig;
import top.shauna.dfs.protocol.HeartBeatProtocol;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 14:37
 * @E-Mail z1023778132@icloud.com
 */
public class SoldierHeartBeat {
    private HeartBeatProtocol heartBeatProtocol;

    public SoldierHeartBeat(){
        heartBeatProtocol = ShaunaRPCHandler.getReferenceProxy(HeartBeatProtocol.class);
    }

    public HeartBeatResponseBean sendHeartBeat(HeartBeatRequestBean heartBeatRequestBean){
        return heartBeatProtocol.registerSoldier(heartBeatRequestBean);
    }

    public HeartBeatResponseBean reportBlocks(HeartBeatRequestBean heartBeatRequestBean){
        return heartBeatProtocol.reportBlocks(heartBeatRequestBean);
    }
}
