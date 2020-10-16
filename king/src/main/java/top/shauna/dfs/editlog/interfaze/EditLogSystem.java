package top.shauna.dfs.editlog.interfaze;

import top.shauna.dfs.kingmanager.bean.LogItem;

import java.io.File;
import java.io.IOException;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 10:32
 * @E-Mail z1023778132@icloud.com
 */
public interface EditLogSystem {
    void initEditLogSystem(String dir);

    void writeEditLog(LogItem logItem) throws IOException;

    int getLogCounter();

    void changeFile() throws IOException;

    void resetCounter();

    File getCurrentFile();
}
