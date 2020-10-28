package top.shauna.dfs.protocol;

import top.shauna.dfs.kingmanager.bean.KingHAMsgBean;
import top.shauna.dfs.kingmanager.bean.LogItem;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 16:10
 * @E-Mail z1023778132@icloud.com
 */
public interface KingHAProtocol {
    KingHAMsgBean electMaster(KingHAMsgBean msg);

    void addLogItem(LogItem logItem);

    void refreshImage(KingHAMsgBean msg);
}
