package top.shauna.dfs.protocol;

import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 20:47
 * @E-Mail z1023778132@icloud.com
 */
public interface HeartBeatProtocol {
    HeartBeatResponseBean reportHeartBeat(HeartBeatRequestBean heartBeatRequestBean);
}
