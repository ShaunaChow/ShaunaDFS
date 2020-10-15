package top.shauna.dfs.config;

import top.shauna.dfs.starter.Starter;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 19:50
 * @E-Mail z1023778132@icloud.com
 */
public class KingConfig implements Starter {
    @Override
    public void onStart() throws Exception {
        prepareConfig();
    }

    private void prepareConfig() throws IOException {
        String propPath = System.getProperty("properties");
        Properties properties = new Properties();
        InputStream in = KingConfig.class.getClassLoader().getResourceAsStream(propPath);
        properties.load(new InputStreamReader(in,"UTF-8"));
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();
        kingPubConfig.setRootDir(properties.getProperty("rootPath","/tmp/ShaunaDfs"));
        kingPubConfig.setPort(properties.getProperty("port","9000"));
        kingPubConfig.setReplicas(Integer.parseInt(properties.getProperty("replicas","3")));
        int blockSize = Integer.parseInt(properties.getProperty("blockSize", "268435456"));
        kingPubConfig.setBlockSize(blockSize>268435456?268435456:blockSize);
        kingPubConfig.setMaxEditLog(Integer.parseInt(properties.getProperty("maxEditLog","10000")));
        kingPubConfig.setEditLogDirs(properties.getProperty("editLogDirs",kingPubConfig.getRootDir()));
        if (properties.getProperty("threadNums")!=null) {
            kingPubConfig.setThreadPoolNums(Integer.valueOf(properties.getProperty("threadNums")));
        }

        PubConfig rpcConfig = PubConfig.getInstance();
        if (properties.getProperty("shaunaRpc.applicationName")!=null) {
            rpcConfig.setApplicationName(properties.getProperty("shaunaRpc.applicationName"));
        }
        if (properties.getProperty("shaunaRpc.threadNum")!=null) {
            rpcConfig.setThreadPoolNums(Integer.valueOf(properties.getProperty("shaunaRpc.threadNum")));
        }
        if (properties.getProperty("shaunaRpc.timeout")!=null) {
            rpcConfig.setTimeout(Long.valueOf(properties.getProperty("shaunaRpc.timeout")));
        }

        RegisterBean registerBean = new RegisterBean();
        if (properties.getProperty("shaunaRpc.registerBean.potocol")!=null) {
            registerBean.setPotocol(properties.getProperty("shaunaRpc.registerBean.potocol"));
        }else{
            registerBean.setPotocol("zookeeper");
        }
        if (properties.getProperty("shaunaRpc.registerBean.url")!=null) {
            registerBean.setUrl(properties.getProperty("shaunaRpc.registerBean.url"));
        }else{
            registerBean.setUrl("127.0.0.1");
        }
        if (properties.getProperty("shaunaRpc.registerBean.loc")!=null){
            registerBean.setLoc(properties.getProperty("shaunaRpc.registerBean.loc"));
        }
        rpcConfig.setRegisterBean(registerBean);

        FoundBean foundBean = new FoundBean();
        if (properties.getProperty("shaunaRpc.foundBean.potocol")!=null) {
            foundBean.setPotocol(properties.getProperty("shaunaRpc.foundBean.potocol"));
        }else{
            foundBean.setPotocol("zookeeper");
        }
        if (properties.getProperty("shaunaRpc.foundBean.url")!=null) {
            foundBean.setUrl(properties.getProperty("shaunaRpc.foundBean.url"));
        }else{
            foundBean.setUrl("127.0.0.1");
        }
        if (properties.getProperty("shaunaRpc.foundBean.loc")!=null){
            foundBean.setLoc(properties.getProperty("shaunaRpc.foundBean.loc"));
        }
        rpcConfig.setFoundBean(foundBean);

        kingPubConfig.setRpcPubConfig(rpcConfig);
    }
}
