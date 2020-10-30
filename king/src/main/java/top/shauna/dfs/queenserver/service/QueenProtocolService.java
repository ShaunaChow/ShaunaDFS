package top.shauna.dfs.queenserver.service;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.editlog.interfaze.EditLogSystem;
import top.shauna.dfs.kingmanager.QueenManager;
import top.shauna.dfs.kingmanager.bean.CheckPoint;
import top.shauna.dfs.kingmanager.bean.QueenInfo;
import top.shauna.dfs.storage.impl.LocalFileStorage;
import top.shauna.dfs.util.CommonUtil;

import java.io.File;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 19:42
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class QueenProtocolService {
    private EditLogSystem editLogSystem;
    private File fileKeeper;
    private String rootDir;
    private LocalFileStorage fileStorage;
    private QueenManager queenManager;

    public QueenProtocolService( EditLogSystem editLogSystem){
        this.editLogSystem = editLogSystem;
        this.fileKeeper = null;
        this.rootDir = KingPubConfig.getInstance().getRootDir();
        this.fileStorage = LocalFileStorage.getInstance();
        this.queenManager = QueenManager.getInstance();
    }

    public CheckPoint doCheckPoint(CheckPoint checkPoint) throws Exception {
        File imageFile = new File(rootDir+File.separator+"ShaunaImage.dat");
        File editFile;
        if (fileKeeper!=null){
            editFile = fileKeeper;
        }else{
            editFile = editLogSystem.getCurrentFile();
            editLogSystem.changeFile();
            fileKeeper = editFile;
        }
        return CommonUtil.getCheckPoint(imageFile, editFile);
    }

    public void checkPointOk(CheckPoint checkPoint) throws Exception {
        if (checkPoint.getStatus()!=null&&checkPoint.getStatus()>=0){
            File editFile = fileKeeper;
            fileKeeper = null;
            byte[] shaunaImage = checkPoint.getShaunaImage();

            File oldImageFile = new File(rootDir+File.separator+"ShaunaImage.dat");
            File bakImageFile = new File(rootDir+File.separator+"ShaunaImage-bak.dat");
            oldImageFile.renameTo(bakImageFile);

            fileStorage.write(rootDir+File.separator+"ShaunaImage.dat",shaunaImage);
            editLogSystem.resetCounter();

            editFile.delete();
            bakImageFile.delete();
        }else{
            log.error("检查点异常！！！");
        }
    }

    public QueenInfo regist(QueenInfo queenInfo) {
        queenInfo.setId(queenManager.getGenId());
        queenManager.registQueen(queenInfo);
        return queenInfo;
    }

    public QueenInfo heartBeat(QueenInfo queenInfo) {
        if (queenInfo.getId()==null){
            queenInfo.setOK(false);
            return queenInfo;
        }
        QueenInfo keeperInfo = queenManager.getQueenInfo(queenInfo.getId());
        if (keeperInfo==null){
            queenInfo.setOK(false);
        }else{
            queenInfo.setOK(true);
            queenInfo.setMasterQueen(queenManager.getMasterQueen());
            Integer maxEditLog = KingPubConfig.getInstance().getMaxEditLog();
            queenInfo.setNeedCheck(editLogSystem.getLogCounter()>(maxEditLog ==null?10000:maxEditLog));
            keeperInfo.setOK(true);
            keeperInfo.setFreeSpace(queenInfo.getFreeSpace());
            keeperInfo.setIp(queenInfo.getIp());
            keeperInfo.setPort(queenInfo.getPort());
            keeperInfo.setTimeStamp(queenInfo.getTimeStamp());
        }
        return queenInfo;
    }
}
