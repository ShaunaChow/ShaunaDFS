package top.shauna.dfs.monitor.controller;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.ha.KingHAStatus;
import top.shauna.dfs.monitor.bean.ResponseBean;
import top.shauna.dfs.monitor.service.KingService;

/**
 * @Author Shauna.Chou
 * @Date 2020/11/1 15:29
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class KingController {
    private KingService kingService;

    public KingController(){
        kingService = new KingService();
    }

    public ResponseBean FSInfo(){
        try{
            int code = getCode();
            return new ResponseBean(code,kingService.FSInfo());
        }catch (Exception e){
            log.error("FSInfo请求出错:"+e.getMessage());
            return new ResponseBean(400,"未知错误");
        }
    }

    public ResponseBean blocksInfo(){
        try{
            int code = getCode();
            return new ResponseBean(code,kingService.blocksInfo());
        }catch (Exception e){
            log.error("blocksInfo请求出错:"+e.getMessage());
            return new ResponseBean(400,"未知错误");
        }
    }

    public ResponseBean soldiersInfo(){
        try{
            int code = getCode();
            return new ResponseBean(code,kingService.soldiersInfo());
        }catch (Exception e){
            log.error("blocksInfo请求出错:"+e.getMessage());
            return new ResponseBean(400,"未知错误");
        }
    }

    public ResponseBean queenInfo(){
        try{
            int code = getCode();
            return new ResponseBean(code,kingService.queenInfo());
        }catch (Exception e){
            log.error("blocksInfo请求出错:"+e.getMessage());
            return new ResponseBean(400,"未知错误");
        }
    }

    private int getCode(){
        if (KingHAStatus.getInstance().getMaster()!=null&&KingHAStatus.getInstance().getMaster()){
            return 200;
        }else{
            return 201;
        }
    }

    public ResponseBean okk(String s){
        return new ResponseBean(200,s);
    }
}
