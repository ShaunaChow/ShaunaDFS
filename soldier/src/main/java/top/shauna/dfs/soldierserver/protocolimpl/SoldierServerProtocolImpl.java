package top.shauna.dfs.soldierserver.protocolimpl;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.soldiermanager.bean.Block;
import top.shauna.dfs.block.interfaces.BlockHandler;
import top.shauna.dfs.monitor.MonitorProxy;
import top.shauna.dfs.protocol.SoldierServerProtocol;
import top.shauna.dfs.soldiermanager.bean.SoldierResponse;
import top.shauna.dfs.type.SoldierResponseType;
import top.shauna.dfs.util.CommonUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/8 16:32
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class SoldierServerProtocolImpl implements SoldierServerProtocol {
    private BlockHandler blockHandler;
    private ConcurrentHashMap<String,SoldierServerProtocol> connectKeeper;

    public SoldierServerProtocolImpl(){
        blockHandler = MonitorProxy.getInstance().getProxy();
        connectKeeper = new ConcurrentHashMap<>();
    }

    public SoldierServerProtocolImpl(BlockHandler blockHandler){
        this.blockHandler = blockHandler;
    }

    @Override
    public Block getBlock(Block block) {
        try {
            blockHandler.read(block);
            return block;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Block请求出错："+e.getMessage());
            return null;
        }
    }

    @Override
    public SoldierResponse uploadFile(Block block) {
        SoldierResponse soldierResponse = new SoldierResponse();
        try {
            blockHandler.write(block);
            List<ReplicasInfo> replicasInfos = block.getReplicasInfos();
            if (replicasInfos==null||replicasInfos.size()==0) {
                soldierResponse.setRes(SoldierResponseType.SUCCESS);
                soldierResponse.setUuid(block.getUuid());
                return soldierResponse;
            }else{
                SoldierServerProtocol soldierServerProtocol = CommonUtil.getSoldierServerProtocol(replicasInfos.get(0));
                block.getReplicasInfos().remove(0);
                return soldierServerProtocol.uploadFile(block);
            }
        } catch (Exception e) {
            log.error("Block写入出错："+e.getMessage());
            soldierResponse.setRes(SoldierResponseType.UNKNOWN);
            soldierResponse.setUuid(block.getUuid());
            return soldierResponse;
        }
    }
}
