package top.shauna.dfs.kingmanager;

import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.starter.Starter;

import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 16:33
 * @E-Mail z1023778132@icloud.com
 */
public class BlocksManager implements Starter {
    private static volatile BlocksManager blocksManager;

    private BlocksManager(){ }

    public static BlocksManager getInstance(){
        if(blocksManager==null){
            synchronized (BlocksManager.class){
                if(blocksManager==null){
                    blocksManager = new BlocksManager();
                }
            }
        }
        return blocksManager;
    }


    @Override
    public void onStart() throws Exception {

    }

    public List<Block> getBlocks(Long fileLength){
        return null;
    }
}
