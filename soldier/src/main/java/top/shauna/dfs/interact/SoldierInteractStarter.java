package top.shauna.dfs.interact;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.block.MetaKeeper;
import top.shauna.dfs.config.SoldierPubConfig;
import top.shauna.dfs.interact.soldier.SoldierHeartBeat;
import top.shauna.dfs.kingmanager.bean.BackupBean;
import top.shauna.dfs.kingmanager.bean.DeleteBean;
import top.shauna.dfs.kingmanager.bean.Transaction;
import top.shauna.dfs.monitor.MonitorProxy;
import top.shauna.dfs.protocol.SoldierServerProtocol;
import top.shauna.dfs.soldiermanager.bean.Block;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;
import top.shauna.dfs.util.CommonUtil;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:53
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class SoldierInteractStarter implements Starter {
    private ConcurrentHashMap<String,SoldierServerProtocol> connectKeeper;

    public SoldierInteractStarter(){
        connectKeeper = new ConcurrentHashMap<>();
    }

    @Override
    public void onStart() {
        SoldierHeartBeat soldierHeartBeat = SoldierHeartBeat.getInstance();
        try {
            soldierHeartBeat.regist();
            soldierHeartBeat.reportAllBlocks();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("block汇报出错");
            return;
        }
        log.info("block汇报OK");

        CommonThreadPool.threadPool.execute(()->{
            while(true){
                try {
                    TimeUnit.SECONDS.sleep(SoldierPubConfig.getInstance().getHeartBeatTime());
                    soldierHeartBeat.sendHeartBeat();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("发送心跳出现错误："+e.getMessage());
                    MessageUtil.setIdKeeper(-99999);
                }
            }
        });
        log.info("心跳初始化OK");

        CommonThreadPool.threadPool.execute(()->{
            ArrayBlockingQueue<Transaction> undoneTrasactions = MessageUtil.getUndoneTrasactions();
            while(true){
                try {
                    Transaction undone = undoneTrasactions.take();
                    switch (undone.getType()){
                        case BACK_UP:
                            BackupBean msg = (BackupBean) undone.getMsg();
                            SoldierServerProtocol protocol = CommonUtil.getSoldierServerProtocol(msg.getGoodReplicas());
                            Block toSendBlock = new Block();
                            toSendBlock.setFilePath(msg.getFilePath());
                            toSendBlock.setPin(msg.getPin());
                            Block block = protocol.getBlock(toSendBlock);
                            block.setVersion(System.currentTimeMillis());
                            MonitorProxy.getInstance().getProxy().write(block);
                            /** 触发一次汇报Blocks **/
                            soldierHeartBeat.reportBlock(block);
                            break;
                        case DELETE:
                            DeleteBean deleteBean = (DeleteBean) undone.getMsg();
                            MetaKeeper.delete(
                                    SoldierPubConfig.getInstance().getRootDir()+
                                            File.separator+"Meta"+deleteBean.getFilePath()+"_"+deleteBean.getPin()+".block"
                            );
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
