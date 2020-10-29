package top.shauna.dfs.ha.service;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.ha.KingHAStatus;
import top.shauna.dfs.kingmanager.LogManager;
import top.shauna.dfs.kingmanager.ShaunaFSManager;
import top.shauna.dfs.kingmanager.bean.CheckPoint;
import top.shauna.dfs.kingmanager.bean.KingHAMsgBean;
import top.shauna.dfs.kingmanager.bean.LogItem;
import top.shauna.dfs.protocol.KingHAProtocol;
import top.shauna.dfs.storage.util.CheckPointUtil;
import top.shauna.dfs.type.KingHAMsgType;
import top.shauna.dfs.util.CommonUtil;
import top.shauna.dfs.util.KingUtils;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 16:20
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class KingHAProtocolService {
    private LogManager logManager;

    public KingHAProtocolService(){
        logManager = LogManager.getInstance();
        logManager.getEditLogSystem().initEditLogSystem(KingPubConfig.getInstance().getEditLogDirs());
    }

    public void electMaster(KingHAMsgBean msg) throws Exception {
        KingHAStatus kingHAStatus = KingHAStatus.getInstance();
        long id = msg.getId();
        Map<String, KingHAProtocol> keeper = kingHAStatus.getKeeper();
        String ip_port = msg.getIp_port();
        KingHAProtocol connect = getConnect(ip_port);
        if (connect!=null) {
            keeper.put(ip_port, connect);
            if (kingHAStatus.isOk(id)) {
                msg.setMsg(KingHAMsgType.YOU_ARE_OK);
            } else {
                if (kingHAStatus.getMaster()!=null&&kingHAStatus.getMaster()){
                    File imageFile = new File(KingPubConfig.getInstance().getRootDir() + File.separator + "ShaunaImage.dat");
                    List<File> logFiles = CommonUtil.scanEditLogFiles(KingPubConfig.getInstance().getEditLogDirs());
                    CheckPoint checkPoint = CommonUtil.getCheckPoint(imageFile, logFiles.toArray(new File[0]));
                    msg.setCheckPoint(checkPoint);
                    msg.setMsg(KingHAMsgType.NEED_REFRESH);
                }else {
                    msg.setMsg(KingHAMsgType.YOU_ARE_NOOK);
                }
            }
        }else{
            throw new Exception("....");
        }
    }

    private KingHAProtocol getConnect(String ip_port) {
        LocalExportBean localExportBean = new LocalExportBean(
                "netty",
                Integer.parseInt(ip_port.substring(1 + ip_port.indexOf(":"))),
                ip_port.substring(0, ip_port.indexOf(":")));
        KingHAProtocol referenceProxy;
        try{
            referenceProxy = ShaunaRPCHandler.getReferenceProxy(KingHAProtocol.class, localExportBean);
        }catch (Exception e){
            e.printStackTrace();
            referenceProxy = null;
        }
        return referenceProxy;
    }

    public void addLogItem(LogItem logItem) throws IOException {
        logManager.saveLogItem(logItem);
    }

    public void refreshImage(KingHAMsgBean msg) throws Exception {
        CheckPoint checkPoint = msg.getCheckPoint();
        if(checkPoint==null||checkPoint.getStatus()<0) {
            log.error("KING回复的CheckPoint状态出错");
            return;
        }
        KingUtils.deleteLogs();
        byte[] newImage = CommonUtil.dealWithCheckPoint(checkPoint);
        CommonUtil.saveCheckPointLocal(newImage,KingPubConfig.getInstance().getRootDir()+File.separator + "ShaunaImage.dat");
        ShaunaFSManager.getInstance().refreshRoot(new DataInputStream(new ByteArrayInputStream(newImage)));
        log.info("Image Refresh完成!!!");
    }
}
