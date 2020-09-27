package top.shauna.dfs.interact.heartbeat.heartbeatimpl;

import com.alibaba.fastjson.JSON;
import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.protocol.HeartBeatProtocol;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:38
 * @E-Mail z1023778132@icloud.com
 */
public class HeartBeatProtocolImpl implements HeartBeatProtocol {
    @Override
    public HeartBeatResponseBean reportHeartBeat(HeartBeatRequestBean heartBeatRequestBean) {
        HeartBeatResponseBean heartBeatResponseBean = new HeartBeatResponseBean();
        heartBeatResponseBean.setBlockInfos(heartBeatRequestBean.getBlockInfos());
        heartBeatResponseBean.setIp("okkk");
        heartBeatResponseBean.setPort("hahaha");
        heartBeatResponseBean.setTimeStamp(System.currentTimeMillis());
        System.out.println(JSON.toJSONString(heartBeatRequestBean));
        return heartBeatResponseBean;
    }
}
