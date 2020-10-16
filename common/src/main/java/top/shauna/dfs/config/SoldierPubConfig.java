package top.shauna.dfs.config;

import lombok.ToString;
import top.shauna.rpc.config.PubConfig;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 16:23
 * @E-Mail z1023778132@icloud.com
 */
@ToString
public class SoldierPubConfig {

    private SoldierPubConfig(){}

    private static volatile SoldierPubConfig soldierPubConfig;

    public static SoldierPubConfig getInstance(){
        if(soldierPubConfig ==null){
            synchronized (SoldierPubConfig.class){
                if(soldierPubConfig ==null){
                    soldierPubConfig = new SoldierPubConfig();
                }
            }
        }
        return soldierPubConfig;
    }

    private String rootDir;
    private String port;
    private PubConfig rpcPubConfig;
    private Integer threadPoolNums;
    private Integer heartBeatTime;

    public Integer getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(Integer heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    public Integer getThreadPoolNums() {
        return threadPoolNums;
    }

    public void setThreadPoolNums(Integer threadPoolNums) {
        this.threadPoolNums = threadPoolNums;
    }

    public PubConfig getRpcPubConfig() {
        return rpcPubConfig;
    }

    public void setRpcPubConfig(PubConfig rpcPubConfig) {
        this.rpcPubConfig = rpcPubConfig;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }
}
