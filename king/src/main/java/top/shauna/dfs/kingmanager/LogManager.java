package top.shauna.dfs.kingmanager;

import top.shauna.dfs.editlog.factory.EditLogSystemFactory;
import top.shauna.dfs.editlog.interfaze.EditLogSystem;
import top.shauna.dfs.ha.KingHAStatus;
import top.shauna.dfs.kingmanager.bean.LogItem;
import top.shauna.dfs.protocol.KingHAProtocol;

import java.io.IOException;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/14 20:27
 * @E-Mail z1023778132@icloud.com
 */
public class LogManager {
    private EditLogSystem editLogSystem;
    private KingHAStatus kingHAStatus;

    private static volatile LogManager logManager;

    private LogManager() {
        editLogSystem = EditLogSystemFactory.getEditLogSystem();
        kingHAStatus = KingHAStatus.getInstance();
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
        if (kingHAStatus.getMaster()!=null&&kingHAStatus.getMaster()) {
            for (KingHAProtocol kingHAProtocol : kingHAStatus.getKeeper().values()) {
                try {
                    kingHAProtocol.addLogItem(logItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public EditLogSystem getEditLogSystem() {
        return editLogSystem;
    }
}
