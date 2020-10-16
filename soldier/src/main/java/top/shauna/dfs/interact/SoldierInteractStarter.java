package top.shauna.dfs.interact;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.bean.HeartBeatRequestBean;
import top.shauna.dfs.bean.HeartBeatResponseBean;
import top.shauna.dfs.config.SoldierPubConfig;
import top.shauna.dfs.interact.heartbeat.SoldierHeartBeat;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;

import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:53
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class SoldierInteractStarter implements Starter {

    @Override
    public void onStart() {
        CommonThreadPool.threadPool.execute(()->{
            SoldierHeartBeat soldierHeartBeat = new SoldierHeartBeat();
            while(true){
                try {
                    HeartBeatRequestBean heartBeatRequestBean = MessageUtil.getHeartBeatRequestBean();
                    HeartBeatResponseBean heartBeatResponseBean = soldierHeartBeat.sendHeartBeat(heartBeatRequestBean);
                    MessageUtil.dealWithResponse(heartBeatResponseBean);
                    TimeUnit.SECONDS.sleep(SoldierPubConfig.getInstance().getHeartBeatTime());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("发送心跳出现错误："+e.getMessage());
                }
            }
        });
        log.info("心跳初始化OK");
    }
}
