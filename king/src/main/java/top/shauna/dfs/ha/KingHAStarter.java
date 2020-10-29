package top.shauna.dfs.ha;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.ha.impl.KingHAProtocolImpl;
import top.shauna.dfs.interact.client.ClientProtocolStarter;
import top.shauna.dfs.interact.soldier.KingHeartBeatStarter;
import top.shauna.dfs.kingmanager.ManagerStarter;
import top.shauna.dfs.kingmanager.ShaunaFSManager;
import top.shauna.dfs.kingmanager.bean.CheckPoint;
import top.shauna.dfs.kingmanager.bean.KingHAMsgBean;
import top.shauna.dfs.protocol.KingHAProtocol;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.util.CommonUtil;
import top.shauna.dfs.util.KingUtils;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.bean.ServiceBean;
import top.shauna.rpc.service.ShaunaRPCHandler;
import top.shauna.rpc.supports.ZKSupportKit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 16:40
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class KingHAStarter implements Starter {
    private KingPubConfig kingPubConfig;
    private String ip;
    private String port;
    private KingHAStatus kingHAStatus;
    private ZKSupportKit zkSupportKit;
    private Map<String,KingHAProtocol> keeper;
    private List<String> ha;
    private ServiceBean serviceBean;

    public KingHAStarter(){
        kingPubConfig = KingPubConfig.getInstance();
        ip = CommonUtil.getLocalHostIp();
        port = kingPubConfig.getHaPort();
        kingHAStatus = KingHAStatus.getInstance();
        zkSupportKit = new ZKSupportKit(kingPubConfig.getRpcPubConfig().getFoundBean().getUrl(),5000);
        keeper = kingHAStatus.getKeeper();
        ha = kingPubConfig.getHa();
    }


    @Override
    public void onStart() throws Exception {
        ha = kingPubConfig.getHa();
        if (ha==null||ha.size()<=1){        /** 不启动高可用 **/
            startThisKing();
            registThisKing();
        }else{                              /** 高可用模式 **/
            String thisKing = ip+":"+port;
            if (ha.contains(thisKing)){
                kingHAStatus.setNextMaster(true);
                kingHAStatus.setMaster(false);
                kingHAStatus.setId(System.currentTimeMillis());
                startThisKing();
                serviceBean = publishHAProtocol();
                ha.remove(thisKing);
                while(connectAll()<1){      /** 至少需要一个僚机 **/
                    log.info("高可用模式----等待队友上线...");
                    TimeUnit.SECONDS.sleep(2);
                }
                vote();
                if (kingHAStatus.becomeMaster()){
                    ShaunaRPCHandler.doRegister(serviceBean);
                    registThisKing();
                    refreshAll();
                }else {
                    zkSupportKit.subscribeChildChanges(top.shauna.rpc.util.CommonUtil.getZookeeperPath(KingHAProtocol.class),
                            (parentPath, currentChilds) -> doChange(parentPath, currentChilds));
                }
            }else{
                throw new Exception("本机不在高可用集群列表中!!!");
            }
        }
    }

    private void refreshAll() throws Exception {
        File imageFile = new File(KingPubConfig.getInstance().getRootDir() + File.separator + "ShaunaImage.dat");
        List<File> logFiles = CommonUtil.scanEditLogFiles(KingPubConfig.getInstance().getEditLogDirs());
        CheckPoint checkPoint = CommonUtil.getCheckPoint(imageFile, logFiles.toArray(new File[0]));
        KingHAMsgBean msgBean = new KingHAMsgBean(ip+":"+port, kingHAStatus.getId(), null,checkPoint);
        Iterator<KingHAProtocol> iterator = keeper.values().iterator();
        while (iterator.hasNext()) {
            KingHAProtocol haProtocol = iterator.next();
            try {
                haProtocol.refreshImage(msgBean);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private int connectAll(){
        int count = 0;
        for (String s : ha) {
            LocalExportBean localExportBean = new LocalExportBean(
                    "netty",
                    Integer.parseInt(s.substring(1 + s.indexOf(":"))),
                    s.substring(0, s.indexOf(":")));
            KingHAProtocol referenceProxy;
            try{
                referenceProxy = ShaunaRPCHandler.getReferenceProxy(KingHAProtocol.class, localExportBean);
                count++;
                keeper.put(s,referenceProxy);
            }catch (Exception e){
                log.info("僚机未准备好");
            }
        }
        return count;
    }

    private void doChange(String parentPath, List<String> childs) throws Exception {
        if (childs==null||childs.size()==0){
            kingHAStatus.setNextMaster(true);
            kingHAStatus.setMaster(false);
            kingHAStatus.setId(System.currentTimeMillis());
            vote();
            if (kingHAStatus.becomeMaster()){
                ShaunaRPCHandler.doRegister(serviceBean);
                registThisKing();
                startThisKing();
            }
        }
    }

    private void vote(){
        KingHAMsgBean msgBean = new KingHAMsgBean(ip+":"+port, kingHAStatus.getId(), null,null);
        Iterator<KingHAProtocol> iterator = keeper.values().iterator();
        while (iterator.hasNext()){
            KingHAProtocol haProtocol = iterator.next();
            if (haProtocol==null) {
                continue;
            }else{
                try {
                    KingHAMsgBean resp = haProtocol.electMaster(msgBean);
                    switch (resp.getMsg()) {
                        case YOU_ARE_OK:
                            log.info("投票+1");
                            break;
                        case YOU_ARE_NOOK:
                            kingHAStatus.setNextMaster(false);
                            break;
                        case NEED_REFRESH:
                            kingHAStatus.setNextMaster(false);
                            KingUtils.deleteLogs();
                            byte[] newImage = CommonUtil.dealWithCheckPoint(resp.getCheckPoint());
                            CommonUtil.saveCheckPointLocal(newImage,KingPubConfig.getInstance().getRootDir()+File.separator+"ShaunaImage.dat");
                            ShaunaFSManager.getInstance().refreshRoot(new DataInputStream(new ByteArrayInputStream(newImage)));
                            log.info("Image Refresh完成!!!");
                            break;
                        case ERROR:
                            log.error("未知错误");
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private ServiceBean publishHAProtocol() {
        LocalExportBean localExportBean = new LocalExportBean();
        localExportBean.setIp(ip);
        localExportBean.setPort(Integer.parseInt(port));
        return ShaunaRPCHandler.publishServiceBean(KingHAProtocol.class,new KingHAProtocolImpl(),localExportBean,false);
    }

    private void startThisKing() throws Exception {
        ManagerStarter.getInstance().onStart();

        KingHeartBeatStarter.getInstance().onStart();

        ClientProtocolStarter.getInstance().onStart();
    }

    private void registThisKing() {
        ManagerStarter.getInstance().doRegist();

        KingHeartBeatStarter.getInstance().doRegist();

        ClientProtocolStarter.getInstance().doRegist();
    }
}
