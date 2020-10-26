package top.shauna.dfs.interact.client;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.interact.client.impl.ClientProtocolImpl;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.dfs.starter.Starter;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 20:46
 * @E-Mail z1023778132@icloud.com
 */
public class ClientProtocolStarter implements Starter {
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
        localExportBean.setPort(Integer.parseInt(KingPubConfig.getInstance().getClientServerPort()));

        ShaunaRPCHandler.publishServiceBean(ClientProtocol.class, new ClientProtocolImpl(),localExportBean);
    }
}
