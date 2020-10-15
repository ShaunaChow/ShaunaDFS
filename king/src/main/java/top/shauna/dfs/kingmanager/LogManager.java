package top.shauna.dfs.kingmanager;

import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.editlog.factory.EditLogSystemFactory;
import top.shauna.dfs.editlog.interfaze.EditLogSystem;
import top.shauna.dfs.kingmanager.bean.LogItem;

import java.io.IOException;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/14 20:27
 * @E-Mail z1023778132@icloud.com
 */
public class LogManager {
    private EditLogSystem editLogSystem;

    private static volatile LogManager logManager;

    private LogManager() {
        String editLogDir = KingPubConfig.getInstance().getEditLogDirs();
        editLogSystem = EditLogSystemFactory.getEditLogSystem(editLogDir);
    }

    public static LogManager getInstance(){
        if (logManager==null){
            synchronized (LogManager.class){
                if (logManager==null){
                    logManager = new LogManager();
                }
            }
        }
        return logManager;
    }

    public void saveLogItem(LogItem logItem) throws IOException {
        if (logItem==null) return;
        editLogSystem.writeEditLog(logItem);
    }

    public EditLogSystem getEditLogSystem() {
        return editLogSystem;
    }
}
