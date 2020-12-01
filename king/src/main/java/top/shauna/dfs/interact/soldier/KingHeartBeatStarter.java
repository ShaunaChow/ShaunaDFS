package top.shauna.dfs.interact.soldier;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.interact.soldier.impl.HeartBeatProtocolImpl;
import top.shauna.dfs.protocol.HeartBeatProtocol;
import top.shauna.dfs.starter.Starter;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.bean.ServiceBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:35
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class KingHeartBeatStarter implements Starter {
    private static KingHeartBeatStarter kingHeartBeatStarter = new KingHeartBeatStarter();

    private KingHeartBeatStarter(){}

    public static KingHeartBeatStarter getInstance(){
        return kingHeartBeatStarter;
    }

    private ServiceBean serviceBean;

    @Override
    public void onStart() {
        LocalExportBean localExportBean = new LocalExportBean();
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();

        localExportBean.setProtocol("netty");
        localExportBean.setIp(kingPubConfig.getExportIP());
        localExportBean.setPort(Integer.parseInt(kingPubConfig.getSoldierServerPort()));
        serviceBean = ShaunaRPCHandler.publishServiceBean(HeartBeatProtocol.class, new HeartBeatProtocolImpl(),localExportBean,false);
    }

    public void doRegist(){
        if (serviceBean==null){
            log.error("请先onStart()");
            return;
        }
        ShaunaRPCHandler.doRegister(serviceBean);
    }
}
