package top.shauna.dfs.protocol;

import top.shauna.dfs.kingmanager.bean.CheckPoint;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 19:09
 * @E-Mail z1023778132@icloud.com
 */
public interface QueenProtocol {
    boolean needCheckPoint();

    CheckPoint doCheckPoint();

    void checkPointOk(CheckPoint checkPoint);
}
