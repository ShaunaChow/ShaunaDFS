package top.shauna.dfs.protocol;

import top.shauna.dfs.kingmanager.bean.CheckPoint;
import top.shauna.dfs.kingmanager.bean.QueenInfo;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 19:09
 * @E-Mail z1023778132@icloud.com
 */
public interface QueenProtocol {
    QueenInfo regist(QueenInfo queenInfo);

    QueenInfo heartBeat(QueenInfo queenInfo);

    CheckPoint doCheckPoint(CheckPoint checkPoint);

    void checkPointOk(CheckPoint checkPoint);
}
