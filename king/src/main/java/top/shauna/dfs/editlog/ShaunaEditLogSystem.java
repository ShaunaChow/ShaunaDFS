package top.shauna.dfs.editlog;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.editlog.interfaze.EditLogSystem;
import top.shauna.dfs.kingmanager.bean.LogItem;

import java.io.*;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 10:32
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ShaunaEditLogSystem implements EditLogSystem {
    private DataOutputStream fileOutputStream;
    private String dir;
    private int filePin = -999;
    private int logCounter;

    public ShaunaEditLogSystem(){

    }

    @Override
    public void initEditLogSystem(String dir){
        this.dir = dir;
        if (filePin<0) filePin = 0;
        try {
            if (fileOutputStream==null) {
                File file = getEmptyFile(dir);
                fileOutputStream = new DataOutputStream(new FileOutputStream(file));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("编辑日志目录打不开哦！！！");
        }
        logCounter = 0;
    }

    private File getEmptyFile(String path) {
        File file = new File(path +File.separator+ "edit_" + filePin + ".log");
        while (file.exists()){
            filePin++;
            file = new File(path +File.separator+ "edit_" + filePin + ".log");
        }
        return file;
    }

    @Override
    public synchronized void writeEditLog(LogItem logItem) throws IOException {
        System.out.println("weizhi2 "+logItem);
        System.out.println("weizhi2 "+fileOutputStream.toString());
        System.out.println("weizhi2 "+dir);
        if (logItem.write(fileOutputStream)) {
            logCounter++;
        }
    }

    @Override
    public int getLogCounter() {
        return logCounter;
    }

    @Override
    public synchronized void changeFile() throws IOException {
        filePin++;
        fileOutputStream.close();
        File file = getEmptyFile(dir);
        fileOutputStream = new DataOutputStream(new FileOutputStream(file));
    }

    @Override
    public void resetCounter() {
        logCounter = 0;
    }

    @Override
    public File getCurrentFile() {
        return new File(dir+File.separator+"edit_"+filePin+".log");
    }
}
