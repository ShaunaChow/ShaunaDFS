package top.shauna.dfs.ha.service;

import top.shauna.dfs.ha.KingHAStatus;
import top.shauna.dfs.kingmanager.LogManager;
import top.shauna.dfs.kingmanager.bean.KingHAMsgBean;
import top.shauna.dfs.kingmanager.bean.LogItem;
import top.shauna.dfs.protocol.KingHAProtocol;
import top.shauna.dfs.type.KingHAMsgType;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.IOException;
import java.util.Map;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 16:20
 * @E-Mail z1023778132@icloud.com
 */
public class KingHAProtocolService {
    private LogManager logManager;

    public KingHAProtocolService(){
        logManager = LogManager.getInstance();
    }

    public void electMaster(KingHAMsgBean msg) {
        KingHAStatus kingHAStatus = KingHAStatus.getInstance();
        Map<String, KingHAProtocol> keeper = kingHAStatus.getKeeper();
        String ip_port = msg.getIp_port();
        if (!keeper.containsKey(ip_port)||keeper.get(ip_port)==null){
            KingHAProtocol connect = getConnect(ip_port);
            if (connect!=null) {
                keeper.put(ip_port, connect);
            }
        }
        long id = msg.getId();
        if (kingHAStatus.isOk(id)){
            msg.setMsg(KingHAMsgType.YOU_ARE_OK);
        }else{
            msg.setMsg(KingHAMsgType.YOU_ARE_NOOK);
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
}
