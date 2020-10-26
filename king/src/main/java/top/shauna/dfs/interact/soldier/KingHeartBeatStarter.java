package top.shauna.dfs.interact.soldier;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.interact.soldier.impl.HeartBeatProtocolImpl;
import top.shauna.dfs.protocol.HeartBeatProtocol;
import top.shauna.dfs.starter.Starter;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:35
 * @E-Mail z1023778132@icloud.com
 */
public class KingHeartBeatStarter implements Starter {
    @Override
    public void onStart() {
        LocalExportBean localExportBean = new LocalExportBean();
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostAddress;
        if(localHost!=null)
            hostAddress = localHost.getHostAddress();
        else
            hostAddress = "127.0.0.1";

        localExportBean.setProtocol("netty");
        localExportBean.setIp(hostAddress);
        localExportBean.setPort(Integer.parseInt(KingPubConfig.getInstance().getSoldierServerPort()));

        ShaunaRPCHandler.publishServiceBean(HeartBeatProtocol.class, new HeartBeatProtocolImpl(),localExportBean);
    }
}
