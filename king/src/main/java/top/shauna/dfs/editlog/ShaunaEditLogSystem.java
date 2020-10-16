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
    private int filePin;
    private int logCounter;

    public ShaunaEditLogSystem(){

    }

    @Override
    public void initEditLogSystem(String dir){
        this.dir = dir;
        filePin = 0;
        try {
            fileOutputStream = new DataOutputStream(new FileOutputStream(dir+File.separator+"edit_"+filePin+".log",true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("编辑日志目录打不开哦！！！");
        }
        logCounter = 0;
    }

    @Override
    public synchronized void writeEditLog(LogItem logItem) throws IOException {
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
        fileOutputStream = new DataOutputStream(new FileOutputStream(dir+File.separator+"edit_"+filePin+".log",true));
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
