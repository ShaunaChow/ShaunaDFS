package top.shauna.dfs.monitor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.shauna.dfs.ha.KingHAStatus;
import top.shauna.dfs.monitor.bean.ResponseBean;
import top.shauna.dfs.monitor.service.KingService;

/**
 * @Author Shauna.Chou
 * @Date 2020/11/1 15:29
 * @E-Mail z1023778132@icloud.com
 */
@Controller
@Slf4j
public class KingController {
    @Autowired
    private KingService kingService;

    @GetMapping("fsinfo")
    @ResponseBody
    public ResponseBean FSInfo(){
        try{
            int code = getCode();
            return new ResponseBean(code,kingService.FSInfo());
        }catch (Exception e){
            log.error("FSInfo请求出错:"+e.getMessage());
            return new ResponseBean(400,"未知错误");
        }
    }

    @GetMapping("blocksinfo")
    @ResponseBody
    public ResponseBean blocksInfo(){
        try{
            int code = getCode();
            return new ResponseBean(code,kingService.blocksInfo());
        }catch (Exception e){
            log.error("blocksInfo请求出错:"+e.getMessage());
            return new ResponseBean(400,"未知错误");
        }
    }

    @GetMapping("soldiersinfo")
    @ResponseBody
    public ResponseBean soldiersInfo(){
        try{
            int code = getCode();
            return new ResponseBean(code,kingService.soldiersInfo());
        }catch (Exception e){
            log.error("blocksInfo请求出错:"+e.getMessage());
            return new ResponseBean(400,"未知错误");
        }
    }

    @GetMapping("queeninfo")
    @ResponseBody
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
}
