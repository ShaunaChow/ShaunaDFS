package top.shauna.dfs.interact.heartbeat.impl;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.interact.heartbeat.service.HeartBeatProtocolService;
import top.shauna.dfs.protocol.HeartBeatProtocol;
import top.shauna.dfs.type.HeartBeatResponseType;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:38
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class HeartBeatProtocolImpl implements HeartBeatProtocol {
    private HeartBeatProtocolService heartBeatProtocolService = new HeartBeatProtocolService();

    @Override
    public HeartBeatResponseBean reportHeartBeat(HeartBeatRequestBean heartBeatRequestBean) {
        HeartBeatResponseBean heartBeatResponseBean = new HeartBeatResponseBean();
        try {
            heartBeatProtocolService.reportHeartBeat(heartBeatRequestBean);
        } catch (Exception e) {
            log.error("心跳出错："+e.getMessage());
            heartBeatResponseBean.setRes(HeartBeatResponseType.UNKNOWN);
            return heartBeatResponseBean;
        }
        heartBeatResponseBean.setBlockInfos(heartBeatRequestBean.getBlockInfos());
        heartBeatResponseBean.setTimeStamp(System.currentTimeMillis());
        heartBeatResponseBean.setRes(HeartBeatResponseType.SUCCESS);
        return heartBeatResponseBean;
    }
}
