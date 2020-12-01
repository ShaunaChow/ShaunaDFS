package top.shauna.dfs.config;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.starter.Starter;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.RegisterBean;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 19:50
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class SoldierConfig implements Starter {
    @Override
    public void onStart() throws Exception {
        prepareConfig();
    }

    private void prepareConfig() throws Exception {
        String propPath = System.getProperty("properties");
        Properties properties = new Properties();
        InputStream in;
        try{
            in = new FileInputStream(propPath);
        }catch (Exception e){
            try{
                in = SoldierConfig.class.getClassLoader().getResourceAsStream(propPath);
            }catch (Exception e2){
                log.error("配置文件路径出错");
                throw new Exception("配置文件路径出错");
            }
        }
        properties.load(new InputStreamReader(in,"UTF-8"));
        SoldierPubConfig soldierPubConfig = SoldierPubConfig.getInstance();
        soldierPubConfig.setRootDir(properties.getProperty("rootPath","/tmp/ShaunaDfs"));
        soldierPubConfig.setPort(properties.getProperty("port","9001"));
        soldierPubConfig.setHeartBeatTime(Integer.parseInt(properties.getProperty("heartBeatTime","2")));
        if (properties.getProperty("threadNums")!=null) {
            soldierPubConfig.setThreadPoolNums(Integer.valueOf(properties.getProperty("threadNums")));
        }

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
        soldierPubConfig.setExportIP(properties.getProperty("exportIP",hostAddress));


        top.shauna.rpc.config.PubConfig rpcConfig = top.shauna.rpc.config.PubConfig.getInstance();
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
        if (properties.getProperty("shaunaRpc.registerBean.loc")!=null){//&&!properties.getProperty("shaunaRpc.registerBean.loc").equals("")) {
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
        if (properties.getProperty("shaunaRpc.foundBean.loc")!=null){//&&!properties.getProperty("shaunaRpc.foundBean.loc").equals("")) {
            foundBean.setLoc(properties.getProperty("shaunaRpc.foundBean.loc"));
        }
        rpcConfig.setFoundBean(foundBean);

        soldierPubConfig.setRpcPubConfig(rpcConfig);
    }
}
