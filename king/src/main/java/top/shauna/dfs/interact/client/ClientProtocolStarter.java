package top.shauna.dfs.interact.client;

import top.shauna.dfs.interact.client.impl.ClientProtocolImpl;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.dfs.starter.Starter;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;
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
        //prepareRpcConfig();

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
        localExportBean.setPort(9001);

        ShaunaRPCHandler.publishServiceBean(ClientProtocol.class, new ClientProtocolImpl(),localExportBean);
    }

    private void prepareRpcConfig() {
        PubConfig pubConfig = PubConfig.getInstance();
        if (pubConfig.getRegisterBean()==null) {
            RegisterBean registerBean = new RegisterBean("zookeeper","39.105.89.185:2181",null);
            pubConfig.setRegisterBean(registerBean);
        }
        if (pubConfig.getFoundBean()==null) {
            RegisterBean registerBean = pubConfig.getRegisterBean();
            FoundBean foundBean = new FoundBean(
                    registerBean.getPotocol(),
                    registerBean.getUrl(),
                    registerBean.getLoc()
            );
            pubConfig.setFoundBean(foundBean);
        }
    }
}
