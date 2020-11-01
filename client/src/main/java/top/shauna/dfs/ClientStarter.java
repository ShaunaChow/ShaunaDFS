package top.shauna.dfs;

import top.shauna.dfs.service.ClientService;
import top.shauna.dfs.service.impl.ClientServiceImpl;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 21:52
 * @E-Mail z1023778132@icloud.com
 */
public class ClientStarter {

    private static void preparePubConfig() {
        PubConfig pubConfig = PubConfig.getInstance();
        pubConfig.setTimeout(100000L);
        if (pubConfig.getRegisterBean() == null) {
            RegisterBean registerBean = new RegisterBean("zookeeper", "39.105.89.185:2181", null);
            pubConfig.setRegisterBean(registerBean);
        }
        if (pubConfig.getFoundBean() == null) {
            RegisterBean registerBean = pubConfig.getRegisterBean();
            FoundBean foundBean = new FoundBean(
                    registerBean.getPotocol(),
                    registerBean.getUrl(),
                    registerBean.getLoc()
            );
            pubConfig.setFoundBean(foundBean);
        }
    }

    public static ClientService getClientService(){
        return new ClientServiceImpl();
    }
}
