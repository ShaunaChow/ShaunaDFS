package top.shauna.dfs.queenserver.service;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.editlog.interfaze.EditLogSystem;
import top.shauna.dfs.kingmanager.bean.CheckPoint;
import top.shauna.dfs.storage.impl.LocalFileStorage;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 19:42
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class QueenProtocolService {
    private EditLogSystem editLogSystem;
    private ConcurrentHashMap<Long,File> fileKeeper;
    private String rootDir;
    private LocalFileStorage fileStorage;

    public QueenProtocolService( EditLogSystem editLogSystem){
        this.editLogSystem = editLogSystem;
        this.fileKeeper = new ConcurrentHashMap<>();
        this.rootDir = KingPubConfig.getInstance().getRootDir();
        fileStorage = LocalFileStorage.getInstance();
    }

    public boolean needCheckPoint() {
        Integer maxEditLog = KingPubConfig.getInstance().getMaxEditLog();
        return editLogSystem.getLogCounter()>(maxEditLog ==null?10000:maxEditLog);
    }

    public CheckPoint doCheckPoint() throws Exception {
        File imageFile = new File(rootDir+File.separator+"ShaunaImage.dat");
        File editFile = editLogSystem.getCurrentFile();
        editLogSystem.changeFile();
        Long id = getUniqueId();
        fileKeeper.put(id,editFile);
        return new CheckPoint(id, fileStorage.read(imageFile), fileStorage.read(editFile), 1);
    }

    public void checkPointOk(CheckPoint checkPoint) throws Exception {
        if (checkPoint.getStatus()!=null&&checkPoint.getStatus()>=0){
            Long id = checkPoint.getUuid();
            File editFile = fileKeeper.get(id);
            byte[] shaunaImage = checkPoint.getShaunaImage();

            File oldImageFile = new File(rootDir+File.separator+"ShaunaImage.dat");
            File bakImageFile = new File(rootDir+File.separator+"ShaunaImage-bak.dat");
            oldImageFile.renameTo(bakImageFile);

            fileStorage.write(rootDir+File.separator+"ShaunaImage.dat",shaunaImage);

            editFile.deleteOnExit();
            bakImageFile.deleteOnExit();
        }else{
            log.error("检查点异常！！！");
        }
    }

    private long getUniqueId(){
        long id = System.nanoTime();
        int hashCode = Math.abs(UUID.randomUUID().hashCode());
        id = id|(hashCode<<31);
        return id;
    }
}
