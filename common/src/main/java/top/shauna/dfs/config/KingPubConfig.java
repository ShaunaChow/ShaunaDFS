package top.shauna.dfs.config;

import top.shauna.rpc.config.PubConfig;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 17:13
 * @E-Mail z1023778132@icloud.com
 */
public class KingPubConfig {
    private static volatile KingPubConfig kingPubConfig;

    private KingPubConfig(){}

    public static KingPubConfig getInstance(){
        if(kingPubConfig==null){
            synchronized (KingPubConfig.class){
                if(kingPubConfig==null){
                    kingPubConfig = new KingPubConfig();
                }
            }
        }
        return kingPubConfig;
    }

    private String rootDir;
    private String port;
    private PubConfig rpcPubConfig;
    private Integer threadPoolNums;
    private Integer replicas;
    private Integer blockSize;
    private Integer maxEditLog;
    private String editLogDirs;
    private double blocksFaultRate;
    private Integer blockScanTime;
    private Integer fileUploadTime;
    private Integer soldierFaultTime;

    public Integer getSoldierFaultTime() {
        return soldierFaultTime;
    }

    public void setSoldierFaultTime(Integer soldierFaultTime) {
        this.soldierFaultTime = soldierFaultTime;
    }

    public Integer getFileUploadTime() {
        return fileUploadTime;
    }

    public void setFileUploadTime(Integer fileUploadTime) {
        this.fileUploadTime = fileUploadTime;
    }

    public Integer getBlockScanTime() {
        return blockScanTime;
    }

    public void setBlockScanTime(Integer blockScanTime) {
        this.blockScanTime = blockScanTime;
    }

    public double getBlocksFaultRate() {
        return blocksFaultRate;
    }

    public void setBlocksFaultRate(double blocksFaultRate) {
        this.blocksFaultRate = blocksFaultRate;
    }

    public String getEditLogDirs() {
        return editLogDirs;
    }

    public void setEditLogDirs(String editLogDirs) {
        this.editLogDirs = editLogDirs;
    }

    public Integer getMaxEditLog() {
        return maxEditLog;
    }

    public void setMaxEditLog(Integer maxEditLog) {
        this.maxEditLog = maxEditLog;
    }

    public Integer getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public PubConfig getRpcPubConfig() {
        return rpcPubConfig;
    }

    public void setRpcPubConfig(PubConfig rpcPubConfig) {
        this.rpcPubConfig = rpcPubConfig;
    }

    public Integer getThreadPoolNums() {
        return threadPoolNums;
    }

    public void setThreadPoolNums(Integer threadPoolNums) {
        this.threadPoolNums = threadPoolNums;
    }
}
