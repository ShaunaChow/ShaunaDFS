package top.shauna.dfs.config;

import lombok.ToString;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 16:23
 * @E-Mail z1023778132@icloud.com
 */
@ToString
public class PubConfig {

    private PubConfig(){}

    private static volatile PubConfig pubConfig = null;

    public static PubConfig getInstance(){
        if(pubConfig==null){
            synchronized (PubConfig.class){
                if(pubConfig==null){
                    pubConfig = new PubConfig();
                }
            }
        }
        return pubConfig;
    }

    private String rootDir;
    private String port;
    private top.shauna.rpc.config.PubConfig rpcPubConfig;
    private Integer threadPoolNums;

    public Integer getThreadPoolNums() {
        return threadPoolNums;
    }

    public void setThreadPoolNums(Integer threadPoolNums) {
        this.threadPoolNums = threadPoolNums;
    }

    public top.shauna.rpc.config.PubConfig getRpcPubConfig() {
        return rpcPubConfig;
    }

    public void setRpcPubConfig(top.shauna.rpc.config.PubConfig rpcPubConfig) {
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
