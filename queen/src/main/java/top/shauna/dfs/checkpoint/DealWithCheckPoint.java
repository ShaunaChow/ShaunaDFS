package top.shauna.dfs.checkpoint;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.QueenPubConfig;
import top.shauna.dfs.kingmanager.bean.*;
import top.shauna.dfs.protocol.QueenProtocol;
import top.shauna.dfs.storage.impl.LocalFileStorage;
import top.shauna.dfs.storage.interfaces.StorageEngine;
import top.shauna.dfs.storage.util.CheckPointUtil;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 20:26
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class DealWithCheckPoint {
    private QueenProtocol queenProtocol;
    private StorageEngine storageEngine;
    private String rootDir;
    private int id;

    public DealWithCheckPoint(){
        queenProtocol = ShaunaRPCHandler.getReferenceProxy(QueenProtocol.class);
        storageEngine = LocalFileStorage.getInstance();
        rootDir = QueenPubConfig.getInstance().getEditLogDirs();
        id = -9999;
    }

    public void regist() throws Exception {
        QueenInfo queenInfo = getQueenInfo();
        QueenInfo registRes = queenProtocol.regist(queenInfo);
        if (registRes.getId()==null){
            log.error("注册失败,id为null");
            throw new Exception("注册失败,id为null");
        }else{
            log.error("注册OKKK");
            id = registRes.getId();
        }
    }

    public QueenInfo heartBeat() throws Exception {
        QueenInfo queenInfo = getQueenInfo();
        return queenProtocol.heartBeat(queenInfo);
    }

    public void doCheckPoint() throws Exception {
        CheckPoint checkPoint = queenProtocol.doCheckPoint(new CheckPoint(getUniqueId(),null,null,1));
        if(checkPoint==null||checkPoint.getStatus()<0) {
            log.error("KING回复的CheckPoint状态出错");
            return;
        }
        byte[] image = checkPoint.getShaunaImage();
        DataInputStream imageInput = new DataInputStream(new ByteArrayInputStream(image));
        byte[] editLog = checkPoint.getEditLog();
        DataInputStream logInput = new DataInputStream(new ByteArrayInputStream(editLog));
        INodeDirectory root = CheckPointUtil.loadRootNode(imageInput);
        List<LogItem> editLogs = CheckPointUtil.loadEditLogs(logInput);
        CheckPointUtil.filtEditLogs(editLogs);
        CheckPointUtil.mergeEditLogs(root,editLogs);
        byte[] newImage = CheckPointUtil.saveRootNode(root);
        CheckPoint newCheckPoint = new CheckPoint(checkPoint.getUuid(), newImage, null, 1);
        queenProtocol.checkPointOk(newCheckPoint);
        saveCheckPointLocal(newImage);
    }

    private long getUniqueId(){
        long id = System.nanoTime();
        int hashCode = Math.abs(UUID.randomUUID().hashCode());
        id = id|(hashCode<<31);
        return id;
    }

    public void saveCheckPointLocal(byte[] image) throws Exception {
        storageEngine.write(rootDir+File.separator+"ShaunaImage.dat",image);
    }

    private QueenInfo getQueenInfo() throws UnknownHostException {
        QueenInfo queenInfo = new QueenInfo();
        if (id>=0) queenInfo.setId(id);
        queenInfo.setIp(InetAddress.getLocalHost().getHostAddress());
        queenInfo.setPort(QueenPubConfig.getInstance().getPort());
        queenInfo.setTimeStamp(System.currentTimeMillis());
        long freeSpace = new File(rootDir).getFreeSpace();
        queenInfo.setFreeSpace(freeSpace);
        return queenInfo;
    }
}
