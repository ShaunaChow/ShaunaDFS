package top.shauna.dfs.interact.client;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.interact.client.impl.ClientProtocolImpl;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.dfs.starter.Starter;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.bean.ServiceBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 20:46
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ClientProtocolStarter implements Starter {
    private static ClientProtocolStarter clientProtocolStarter = new ClientProtocolStarter();

    private ClientProtocolStarter(){}

    public static ClientProtocolStarter getInstance(){
        return clientProtocolStarter;
    }

    private ServiceBean serviceBean;

    @Override
    public void onStart() {
        LocalExportBean localExportBean = new LocalExportBean();
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();

        localExportBean.setProtocol("netty");
        localExportBean.setIp(kingPubConfig.getExportIP());
        localExportBean.setPort(Integer.parseInt(kingPubConfig.getClientServerPort()));

        serviceBean = ShaunaRPCHandler.publishServiceBean(ClientProtocol.class, new ClientProtocolImpl(),localExportBean,false);
    }

    public void doRegist(){
        if (serviceBean==null){
            log.error("请先onStart()");
            return;
        }
        ShaunaRPCHandler.doRegister(serviceBean);
    }
}
