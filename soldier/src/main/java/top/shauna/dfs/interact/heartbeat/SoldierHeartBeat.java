package top.shauna.dfs.interact.heartbeat;

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
        prepareRpcConfig();

        return heartBeatProtocol.reportHeartBeat(heartBeatRequestBean);
    }

    private void prepareRpcConfig() {
        if (SoldierPubConfig.getInstance().getRpcPubConfig()==null) {
            top.shauna.rpc.config.PubConfig rpcConfig = top.shauna.rpc.config.PubConfig.getInstance();
            if (rpcConfig.getRegisterBean()==null) {
                RegisterBean registerBean = new RegisterBean("zookeeper","127.0.0.1:2181",null);
                rpcConfig.setRegisterBean(registerBean);
            }
            if (rpcConfig.getFoundBean()==null) {
                RegisterBean registerBean = rpcConfig.getRegisterBean();
                FoundBean foundBean = new FoundBean(
                        registerBean.getPotocol(),
                        registerBean.getUrl(),
                        registerBean.getLoc()
                );
                rpcConfig.setFoundBean(foundBean);
            }
            SoldierPubConfig.getInstance().setRpcPubConfig(rpcConfig);
        }
    }
}
