package top.shauna.dfs.protocol;

import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.kingmanager.bean.SoldierInfo;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 20:47
 * @E-Mail z1023778132@icloud.com
 */
public interface HeartBeatProtocol {
    HeartBeatResponseBean reportBlocks(HeartBeatRequestBean heartBeatRequestBean);

    HeartBeatResponseBean registerSoldier(HeartBeatRequestBean heartBeatRequestBean);
}
