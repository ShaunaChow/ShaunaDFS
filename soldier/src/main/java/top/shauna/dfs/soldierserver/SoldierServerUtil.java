package top.shauna.dfs.soldierserver;

import top.shauna.dfs.config.SoldierPubConfig;
import top.shauna.dfs.soldierserver.protocolimpl.SoldierServerProtocolImpl;
import top.shauna.dfs.protocol.SoldierServerProtocol;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 21:56
 * @E-Mail z1023778132@icloud.com
 */
public class SoldierServerUtil {

    public static void startServer(){
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

        int port;
        if(SoldierPubConfig.getInstance().getPort()!=null&&!SoldierPubConfig.getInstance().getPort().equals(""))
            port = Integer.parseInt(SoldierPubConfig.getInstance().getPort());
        else
            port = 9001;

        localExportBean.setProtocol("netty");
        localExportBean.setIp(hostAddress);
        localExportBean.setPort(port);

        ShaunaRPCHandler.publishServiceBean(SoldierServerProtocol.class,new SoldierServerProtocolImpl(),localExportBean);
    }
}
