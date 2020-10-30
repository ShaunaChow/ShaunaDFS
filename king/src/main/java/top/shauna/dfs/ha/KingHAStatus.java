package top.shauna.dfs.ha;

import top.shauna.dfs.protocol.KingHAProtocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 16:18
 * @E-Mail z1023778132@icloud.com
 */
public class KingHAStatus {
    private static volatile KingHAStatus kingHAStatus;

    private KingHAStatus(){
        keeper = new ConcurrentHashMap<>();
    }

    public static KingHAStatus getInstance(){
        if (kingHAStatus==null){
            synchronized (KingHAStatus.class){
                if (kingHAStatus==null){
                    kingHAStatus = new KingHAStatus();
                }
            }
        }
        return kingHAStatus;
    }

    private Boolean master;
    private Long id;
    private Boolean nextMaster;
    private Map<String,KingHAProtocol> keeper;

    public synchronized boolean becomeMaster(){     /** 加锁！ **/
        if (nextMaster){
            master = true;
            return true;
        }else{
            return false;
        }
    }

    public synchronized boolean isOk(Long id){      /** 加锁！ **/
        if (master||this.id<id){
            return false;
        }else{
            nextMaster = false;
            return true;
        }
    }

    public Map<String, KingHAProtocol> getKeeper() {
        return keeper;
    }

    public void setKeeper(Map<String, KingHAProtocol> keeper) {
        this.keeper = keeper;
    }

    public Boolean getNextMaster() {
        return nextMaster;
    }

    public void setNextMaster(Boolean nextMaster) {
        this.nextMaster = nextMaster;
    }

    public synchronized Boolean getMaster() {
        return master;
    }

    public void setMaster(Boolean master) {
        this.master = master;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
