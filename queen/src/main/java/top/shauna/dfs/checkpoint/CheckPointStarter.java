package top.shauna.dfs.checkpoint;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.QueenPubConfig;
import top.shauna.dfs.kingmanager.bean.QueenInfo;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;

import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/16 22:01
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class CheckPointStarter implements Starter {
    @Override
    public void onStart() throws Exception {
        DealWithCheckPoint checkPoint = new DealWithCheckPoint();

        checkPoint.regist();

        CommonThreadPool.threadPool.execute(()->{
            while (true) {
                try {
                        TimeUnit.SECONDS.sleep(QueenPubConfig.getInstance().getCheckPointTime());
                        QueenInfo queenInfo = checkPoint.heartBeat();
                        if (queenInfo.getOK()==null||!queenInfo.getOK()){
                            checkPoint.regist();
                            continue;
                        }
                        if (queenInfo.getNeedCheck()!=null&&queenInfo.getNeedCheck()) {
                            checkPoint.doCheckPoint();
                        }
                } catch (Exception e) {
                    DealWithCheckPoint.setId(-9999);
                    e.printStackTrace();
                }
            }
        });
    }
}
