package top.shauna.dfs.queenserver.service;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.editlog.interfaze.EditLogSystem;
import top.shauna.dfs.kingmanager.bean.CheckPoint;
import top.shauna.dfs.storage.impl.LocalFileStorage;

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

    public QueenProtocolService( EditLogSystem editLogSystem){
        this.editLogSystem = editLogSystem;
        this.fileKeeper = null;
        this.rootDir = KingPubConfig.getInstance().getRootDir();
        fileStorage = LocalFileStorage.getInstance();
    }

    public boolean needCheckPoint() {
        Integer maxEditLog = KingPubConfig.getInstance().getMaxEditLog();
        return editLogSystem.getLogCounter()>(maxEditLog ==null?10000:maxEditLog);
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
        checkPoint.setShaunaImage(fileStorage.read(imageFile));
        checkPoint.setEditLog(fileStorage.read(editFile));
        checkPoint.setStatus(1);
        return checkPoint;
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
}
