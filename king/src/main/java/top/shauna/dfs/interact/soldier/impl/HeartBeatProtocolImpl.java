package top.shauna.dfs.interact.soldier.impl;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.interact.soldier.service.HeartBeatProtocolService;
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
    public HeartBeatResponseBean regist(HeartBeatRequestBean heartBeatRequestBean) {
        HeartBeatResponseBean heartBeatResponseBean = new HeartBeatResponseBean();
        heartBeatResponseBean.setIp(heartBeatRequestBean.getIp());
        heartBeatResponseBean.setPort(heartBeatRequestBean.getPort());
        try {
            heartBeatProtocolService.regist(heartBeatRequestBean);
        } catch (Exception e) {
            log.error("注测出错："+e.getMessage());
            heartBeatResponseBean.setRes(HeartBeatResponseType.UNKNOWN);
            return heartBeatResponseBean;
        }
        heartBeatResponseBean.setId(heartBeatRequestBean.getId());
        heartBeatResponseBean.setTimeStamp(System.currentTimeMillis());
        heartBeatResponseBean.setRes(HeartBeatResponseType.SUCCESS);
        return heartBeatResponseBean;
    }

    @Override
    public HeartBeatResponseBean reportBlocks(HeartBeatRequestBean heartBeatRequestBean) {
        HeartBeatResponseBean heartBeatResponseBean = new HeartBeatResponseBean();
        heartBeatResponseBean.setIp(heartBeatRequestBean.getIp());
        heartBeatResponseBean.setPort(heartBeatRequestBean.getPort());
        try {
            heartBeatProtocolService.reportBlocks(heartBeatRequestBean);
        } catch (Exception e) {
            log.error("Blocks汇报出错："+e.getMessage());
            heartBeatResponseBean.setRes(HeartBeatResponseType.UNKNOWN);
            return heartBeatResponseBean;
        }
        heartBeatResponseBean.setBlockInfos(heartBeatRequestBean.getBlockInfos());
        heartBeatResponseBean.setTimeStamp(System.currentTimeMillis());
        heartBeatResponseBean.setRes(HeartBeatResponseType.SUCCESS);
        return heartBeatResponseBean;
    }

    @Override
    public HeartBeatResponseBean heartBeat(HeartBeatRequestBean heartBeatRequestBean) {
        HeartBeatResponseBean heartBeatResponseBean = new HeartBeatResponseBean();
        try {
            if (heartBeatProtocolService.heartBeat(heartBeatRequestBean)) {
                heartBeatResponseBean.setTimeStamp(System.currentTimeMillis());
                heartBeatResponseBean.setBlockInfos(heartBeatRequestBean.getBlockInfos());
                heartBeatResponseBean.setTransactions(heartBeatRequestBean.getTransactions());
                heartBeatResponseBean.setRes(HeartBeatResponseType.SUCCESS);
            }else{
                heartBeatResponseBean.setTimeStamp(System.currentTimeMillis());
                heartBeatResponseBean.setRes(HeartBeatResponseType.REPORT_BLOCKS_AGAIN);
            }
        } catch (Exception e) {
            log.error("心跳出错："+e.getMessage());
            heartBeatResponseBean.setRes(HeartBeatResponseType.UNKNOWN);
            return heartBeatResponseBean;
        }
        return heartBeatResponseBean;
    }

}
