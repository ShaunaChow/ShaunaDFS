package top.shauna.dfs.soldierserver.protocolimpl;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.soldiermanager.bean.Block;
import top.shauna.dfs.block.interfaces.BlockHandler;
import top.shauna.dfs.monitor.MonitorProxy;
import top.shauna.dfs.protocol.SoldierServerProtocol;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/8 16:32
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class SoldierServerProtocolImpl implements SoldierServerProtocol {
    private BlockHandler blockHandler;

    public SoldierServerProtocolImpl(){
        blockHandler = new MonitorProxy().getProxy();
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
            log.error("Block请求出错："+e.getMessage());
            return null;
        }
    }
}
