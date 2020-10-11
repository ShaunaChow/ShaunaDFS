package top.shauna.dfs.client.test;

import org.junit.Test;
import top.shauna.dfs.kingmanager.bean.ClientFileInfo;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.IOException;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 17:36
 * @E-Mail z1023778132@icloud.com
 */
public class Test1 {

    @Test
    public void test1(){
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

        ClientProtocol proxy = ShaunaRPCHandler.getReferenceProxy(ClientProtocol.class);

        mkdir("/shauna",proxy);
        mkdir("/shauna/test",proxy);

        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath("/shauna/test/tt1.txt");
        clientFileInfo.setName("tt1.txt");
        clientFileInfo.setFileLength(200L);

        ClientFileInfo uploadFile = proxy.uploadFile(clientFileInfo);
        System.out.println(uploadFile);

        proxy.uploadFileOk(clientFileInfo);

        ClientFileInfo downloadFile = proxy.downloadFile(clientFileInfo);
        System.out.println(downloadFile);

        clientFileInfo.setPath("/shauna");
        clientFileInfo.setName("shauna/");

        ClientFileInfo rmFile = proxy.rmDir(clientFileInfo);
        System.out.println(rmFile);
    }

    @Test
    public void test2() throws IOException {
        PubConfig pubConfig = PubConfig.getInstance();
        pubConfig.setTimeout(100000L);
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

        ClientProtocol proxy = ShaunaRPCHandler.getReferenceProxy(ClientProtocol.class);

        mkdir("/shauna",proxy);
        mkdir("/mago",proxy);
        mkdir("/yizhi",proxy);
        mkdir("/shauna/test",proxy);

        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath("/shauna/test/tt1.txt");
        clientFileInfo.setName("tt1.txt");
        clientFileInfo.setFileLength(200L);

        ClientFileInfo uploadFile = proxy.uploadFile(clientFileInfo);
        System.out.println(uploadFile);

        proxy.uploadFileOk(clientFileInfo);

        System.in.read();
    }

    private void mkdir(String path, ClientProtocol proxy){
        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath(path);
        clientFileInfo.setName(path.substring(path.lastIndexOf("/")+1));
        ClientFileInfo mkdir = proxy.mkdir(clientFileInfo);
        System.out.println(mkdir);
    }
}
