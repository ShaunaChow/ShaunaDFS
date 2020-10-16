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

    public DealWithCheckPoint(){
        queenProtocol = ShaunaRPCHandler.getReferenceProxy(QueenProtocol.class);
        storageEngine = LocalFileStorage.getInstance();
    }

    public byte[] doCheckPoint() throws Exception {
        if (queenProtocol.needCheckPoint()){
            CheckPoint checkPoint = queenProtocol.doCheckPoint(new CheckPoint(getUniqueId(),null,null,1));
            if(checkPoint==null||checkPoint.getStatus()<0) {
                log.error("KING回复的CheckPoint状态出错");
                return new byte[0];
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
            return newImage;
        }
        return null;
    }

    private long getUniqueId(){
        long id = System.nanoTime();
        int hashCode = Math.abs(UUID.randomUUID().hashCode());
        id = id|(hashCode<<31);
        return id;
    }

    public void saveCheckPointLocal(byte[] image) throws Exception {
        String rootDir = QueenPubConfig.getInstance().getEditLogDirs();
        storageEngine.write(rootDir+File.separator+"ShaunaImage.dat",image);
    }
}
