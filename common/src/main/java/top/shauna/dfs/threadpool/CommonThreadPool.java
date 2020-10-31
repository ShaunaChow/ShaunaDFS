package top.shauna.dfs.threadpool;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.config.QueenPubConfig;
import top.shauna.dfs.config.SoldierPubConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:58
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class CommonThreadPool {

    public static ExecutorService threadPool;
    static {
        int cores;
        if(KingPubConfig.getInstance().getThreadPoolNums()!=null&&!KingPubConfig.getInstance().getThreadPoolNums().equals(0)){
            cores = KingPubConfig.getInstance().getThreadPoolNums();
        }else if(SoldierPubConfig.getInstance().getThreadPoolNums()!=null&&!SoldierPubConfig.getInstance().getThreadPoolNums().equals(0)){
            cores = SoldierPubConfig.getInstance().getThreadPoolNums();
        }else if(QueenPubConfig.getInstance().getThreadPoolNums()!=null&&!QueenPubConfig.getInstance().getThreadPoolNums().equals(0)){
            cores = QueenPubConfig.getInstance().getThreadPoolNums();
        }else{
            cores = Runtime.getRuntime().availableProcessors()*5;
        }
        log.info("线程总数："+cores);
        threadPool = new ThreadPoolExecutor(
                cores,
                cores,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(cores/4+1),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
