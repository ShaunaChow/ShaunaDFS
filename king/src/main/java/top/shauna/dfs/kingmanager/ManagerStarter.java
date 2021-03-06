package top.shauna.dfs.kingmanager;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.config.SoldierPubConfig;
import top.shauna.dfs.kingmanager.proxy.ShaunaFSManagerProxy;
import top.shauna.dfs.protocol.QueenProtocol;
import top.shauna.dfs.queenserver.impl.QueenProtocolImpl;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.bean.ServiceBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 16:37
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ManagerStarter implements Starter {
    private static ManagerStarter managerStarter = new ManagerStarter();

    private ManagerStarter(){}

    public static ManagerStarter getInstance(){
        return managerStarter;
    }

    private ServiceBean serviceBean;

    @Override
    public void onStart() throws Exception {
        ShaunaFSManagerProxy.getInstance(LogManager.getInstance()).getProxy().onStart();
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();
        LogManager.getInstance().getEditLogSystem().initEditLogSystem(kingPubConfig.getEditLogDirs());
        LocalExportBean localExportBean = new LocalExportBean();

        localExportBean.setProtocol("netty");
        localExportBean.setIp(kingPubConfig.getExportIP());
        localExportBean.setPort(Integer.parseInt(kingPubConfig.getQueenServerPort()));
        serviceBean = ShaunaRPCHandler.publishServiceBean(
                QueenProtocol.class,
                new QueenProtocolImpl(LogManager.getInstance().getEditLogSystem()),
                localExportBean,
                false
        );
        BlocksManager.getInstance().onStart();
        SoldierManager.getInstance().onStart();
        QueenManager.getInstance().onStart();
    }

    public void doRegist(){
        if (serviceBean==null){
            log.error("请先onStart()");
            return;
        }
        ShaunaRPCHandler.doRegister(serviceBean);
    }
}
