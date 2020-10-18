package top.shauna.dfs.kingmanager;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.proxy.ShaunaFSManagerProxy;
import top.shauna.dfs.protocol.QueenProtocol;
import top.shauna.dfs.queenserver.impl.QueenProtocolImpl;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 16:37
 * @E-Mail z1023778132@icloud.com
 */
public class ManagerStarter implements Starter {
    @Override
    public void onStart() throws Exception {
        ShaunaFSManagerProxy.getInstance(LogManager.getInstance()).getProxy().onStart();
        LogManager.getInstance().getEditLogSystem().initEditLogSystem(KingPubConfig.getInstance().getEditLogDirs());
        CommonThreadPool.threadPool.execute(()->{
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
            localExportBean.setPort(9002);

            ShaunaRPCHandler.publishServiceBean(
                    QueenProtocol.class,
                    new QueenProtocolImpl(LogManager.getInstance().getEditLogSystem()),
                    localExportBean
            );
        });
        BlocksManager.getInstance().onStart();
    }
}
