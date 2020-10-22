package top.shauna.dfs.queenserver.impl;

import top.shauna.dfs.editlog.interfaze.EditLogSystem;
import top.shauna.dfs.kingmanager.bean.CheckPoint;
import top.shauna.dfs.kingmanager.bean.QueenInfo;
import top.shauna.dfs.protocol.QueenProtocol;
import top.shauna.dfs.queenserver.service.QueenProtocolService;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 19:24
 * @E-Mail z1023778132@icloud.com
 */
public class QueenProtocolImpl implements QueenProtocol {
    private QueenProtocolService queenProtocolService;

    public QueenProtocolImpl(EditLogSystem editLogSystem){
        queenProtocolService = new QueenProtocolService(editLogSystem);
    }

    @Override
    public QueenInfo regist(QueenInfo queenInfo) {
        try{
            return queenProtocolService.regist(queenInfo);
        }catch (Exception e){
            e.printStackTrace();
            return queenInfo;
        }
    }

    @Override
    public QueenInfo heartBeat(QueenInfo queenInfo) {
        try{
            return queenProtocolService.heartBeat(queenInfo);
        }catch (Exception e){
            e.printStackTrace();
            return queenInfo;
        }
    }

    @Override
    public CheckPoint doCheckPoint(CheckPoint checkPoint) {
        try {
            return queenProtocolService.doCheckPoint(checkPoint);
        }catch (Exception e){
            e.printStackTrace();
            return new CheckPoint(0L,null,null,-1);
        }
    }

    @Override
    public void checkPointOk(CheckPoint checkPoint) {
        try {
            queenProtocolService.checkPointOk(checkPoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
