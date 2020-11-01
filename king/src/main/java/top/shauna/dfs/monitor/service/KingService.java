package top.shauna.dfs.monitor.service;

import org.springframework.stereotype.Service;
import top.shauna.dfs.kingmanager.BlocksManager;
import top.shauna.dfs.kingmanager.QueenManager;
import top.shauna.dfs.kingmanager.ShaunaFSManager;
import top.shauna.dfs.kingmanager.SoldierManager;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.INodeDirectory;
import top.shauna.dfs.kingmanager.bean.QueenInfo;
import top.shauna.dfs.kingmanager.bean.SoldierInfo;
import top.shauna.dfs.kingmanager.proxy.ShaunaFSManagerProxy;

import java.util.List;
import java.util.Map;

/**
 * @Author Shauna.Chou
 * @Date 2020/11/1 15:32
 * @E-Mail z1023778132@icloud.com
 */
@Service
public class KingService {
    private ShaunaFSManager shaunaFSManager;
    private BlocksManager blocksManager;
    private SoldierManager soldierManager;
    private QueenManager queenManager;

    public KingService(){
        shaunaFSManager = ShaunaFSManagerProxy.getInstance(null).getShaunaFSManager();
        blocksManager = BlocksManager.getInstance();
        soldierManager = SoldierManager.getInstance();
        queenManager = QueenManager.getInstance();
    }

    public INodeDirectory FSInfo() {
        return shaunaFSManager.getRoot();
    }

    public Map<String, List<Block>> blocksInfo() {
        return blocksManager.getBlocksMap();
    }

    public Map<Integer, SoldierInfo> soldiersInfo() {
        return soldierManager.getSoldierInfoMap();
    }

    public Map<Integer, QueenInfo> queenInfo() {
        return queenManager.getQueens();
    }
}
