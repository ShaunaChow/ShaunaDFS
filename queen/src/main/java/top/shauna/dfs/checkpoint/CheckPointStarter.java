package top.shauna.dfs.checkpoint;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.QueenPubConfig;
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

        CommonThreadPool.threadPool.execute(()->{
            try {
                while (true) {
                    byte[] newImage = checkPoint.doCheckPoint();
                    if (newImage == null) {
                        log.info("CheckPoint无需更新");
                    } else {
                        checkPoint.saveCheckPointLocal(newImage);
                        log.info("CheckPoint保存到本地");
                    }
                    TimeUnit.SECONDS.sleep(QueenPubConfig.getInstance().getCheckPointTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
