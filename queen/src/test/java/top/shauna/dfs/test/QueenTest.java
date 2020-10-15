package top.shauna.dfs.test;

import org.junit.Test;
import top.shauna.dfs.checkpoint.DealWithCheckPoint;
import top.shauna.dfs.config.SoldierPubConfig;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.RegisterBean;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 22:19
 * @E-Mail z1023778132@icloud.com
 */
public class QueenTest {

    @Test
    public void test() throws Exception {
        prepareRpcConfig();
        DealWithCheckPoint checkPoint = new DealWithCheckPoint();
        checkPoint.doCheckPoint();
    }

    private void prepareRpcConfig() {
        if (SoldierPubConfig.getInstance().getRpcPubConfig()==null) {
            top.shauna.rpc.config.PubConfig rpcConfig = top.shauna.rpc.config.PubConfig.getInstance();
            rpcConfig.setTimeout(100000L);
            if (rpcConfig.getRegisterBean()==null) {
                RegisterBean registerBean = new RegisterBean("zookeeper","39.105.89.185:2181",null);
                rpcConfig.setRegisterBean(registerBean);
            }
            if (rpcConfig.getFoundBean()==null) {
                RegisterBean registerBean = rpcConfig.getRegisterBean();
                FoundBean foundBean = new FoundBean(
                        registerBean.getPotocol(),
                        registerBean.getUrl(),
                        registerBean.getLoc()
                );
                rpcConfig.setFoundBean(foundBean);
            }
            SoldierPubConfig.getInstance().setRpcPubConfig(rpcConfig);
        }
    }
}
