package top.shauna.dfs.config;

import top.shauna.rpc.config.PubConfig;

import java.util.List;

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
    private String soldierServerPort;
    private String clientServerPort;
    private String queenServerPort;
    private String haPort;
    private PubConfig rpcPubConfig;
    private Integer threadPoolNums;
    private Integer replicas;
    private Integer blockSize;
    private Integer maxEditLog;
    private String editLogDirs;
    private double blocksFaultRate;
    private Integer blockScanTime;
    private Integer fileOutOfTime;
    private Integer soldierFaultTime;
    private Integer soldierScanTime;
    private Integer queenFaultTime;
    private Integer queenScanTime;
    private List<String> ha;
    private String monitorPort;
    private String exportIP;

    public String getExportIP() {
        return exportIP;
    }

    public void setExportIP(String exportIP) {
        this.exportIP = exportIP;
    }

    public String getMonitorPort() {
        return monitorPort;
    }

    public void setMonitorPort(String monitorPort) {
        this.monitorPort = monitorPort;
    }

    public List<String> getHa() {
        return ha;
    }

    public void setHa(List<String> ha) {
        this.ha = ha;
    }

    public String getHaPort() {
        return haPort;
    }

    public void setHaPort(String haPort) {
        this.haPort = haPort;
    }

    public String getClientServerPort() {
        return clientServerPort;
    }

    public void setClientServerPort(String clientServerPort) {
        this.clientServerPort = clientServerPort;
    }

    public String getQueenServerPort() {
        return queenServerPort;
    }

    public void setQueenServerPort(String queenServerPort) {
        this.queenServerPort = queenServerPort;
    }

    public Integer getQueenFaultTime() {
        return queenFaultTime;
    }

    public void setQueenFaultTime(Integer queenFaultTime) {
        this.queenFaultTime = queenFaultTime;
    }

    public Integer getQueenScanTime() {
        return queenScanTime;
    }

    public void setQueenScanTime(Integer queenScanTime) {
        this.queenScanTime = queenScanTime;
    }

    public Integer getSoldierScanTime() {
        return soldierScanTime;
    }

    public void setSoldierScanTime(Integer soldierScanTime) {
        this.soldierScanTime = soldierScanTime;
    }

    public Integer getSoldierFaultTime() {
        return soldierFaultTime;
    }

    public void setSoldierFaultTime(Integer soldierFaultTime) {
        this.soldierFaultTime = soldierFaultTime;
    }

    public Integer getFileOutOfTime() {
        return fileOutOfTime;
    }

    public void setFileOutOfTime(Integer fileOutOfTime) {
        this.fileOutOfTime = fileOutOfTime;
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

    public String getSoldierServerPort() {
        return soldierServerPort;
    }

    public void setSoldierServerPort(String soldierServerPort) {
        this.soldierServerPort = soldierServerPort;
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
