package top.shauna.dfs.ha.impl;

import top.shauna.dfs.ha.service.KingHAProtocolService;
import top.shauna.dfs.kingmanager.bean.KingHAMsgBean;
import top.shauna.dfs.kingmanager.bean.LogItem;
import top.shauna.dfs.protocol.KingHAProtocol;
import top.shauna.dfs.type.KingHAMsgType;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 16:17
 * @E-Mail z1023778132@icloud.com
 */
public class KingHAProtocolImpl implements KingHAProtocol {
    private KingHAProtocolService kingHAProtocolService;

    public KingHAProtocolImpl(){
        kingHAProtocolService = new KingHAProtocolService();
    }

    @Override
    public KingHAMsgBean electMaster(KingHAMsgBean msg) {
        try{
            kingHAProtocolService.electMaster(msg);
        }catch (Exception e){
            e.printStackTrace();
            msg.setMsg(KingHAMsgType.ERROR);
        }
        return msg;
    }

    @Override
    public void addLogItem(LogItem logItem) {
        try{
            kingHAProtocolService.addLogItem(logItem);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
