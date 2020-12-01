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
        SoldierPubConfig soldierPubConfig = SoldierPubConfig.getInstance();

        int port;
        if(soldierPubConfig.getPort()!=null&&!soldierPubConfig.getPort().equals(""))
            port = Integer.parseInt(soldierPubConfig.getPort());
        else
            port = 9001;

        localExportBean.setProtocol("netty");
        localExportBean.setIp(soldierPubConfig.getExportIP());
        localExportBean.setPort(port);

        ShaunaRPCHandler.publishServiceBean(SoldierServerProtocol.class,new SoldierServerProtocolImpl(),localExportBean);
    }
}
