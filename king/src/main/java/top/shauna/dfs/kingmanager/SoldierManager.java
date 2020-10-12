package top.shauna.dfs.kingmanager;

import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.kingmanager.bean.SoldierInfo;
import top.shauna.dfs.starter.Starter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 14:53
 * @E-Mail z1023778132@icloud.com
 */
public class SoldierManager implements Starter {
    private static volatile SoldierManager soldierManager;

    private SoldierManager(){
        soldierInfoMap = new ConcurrentHashMap<>();
        header = new SoldierInfo();
        tailer = new SoldierInfo();
        header.next = tailer;
        tailer.pre = header;
    }

    public static SoldierManager getInstance(){
        if(soldierManager==null){
            synchronized (SoldierManager.class){
                if(soldierManager==null){
                    soldierManager = new SoldierManager();
                }
            }
        }
        return soldierManager;
    }

    private ConcurrentHashMap<String,SoldierInfo> soldierInfoMap;
    private SoldierInfo header;
    private SoldierInfo tailer;

    @Override
    public void onStart() throws Exception {

    }

    public SoldierInfo getSoldierInfo(String ip_port){
        return soldierInfoMap.get(ip_port);
    }

    public void registSoldier(String ip_port,SoldierInfo soldierInfo){
        if(soldierInfoMap.containsKey(ip_port)){
            SoldierInfo info = soldierInfoMap.get(ip_port);
            info.setBlockInfos(soldierInfo.getBlockInfos());
            info.setOK(soldierInfo.getOK());
            info.setTimeStamp(soldierInfo.getTimeStamp());
            adjustSoldierList(info);
        }else{
            soldierInfoMap.put(ip_port, soldierInfo);
            addIntoList(soldierInfo);
        }
    }

    public void adjustSoldierList(SoldierInfo soldierInfo){
        SoldierInfo pre = soldierInfo.pre;
        SoldierInfo next = soldierInfo.next;
        pre.next = next;
        next.pre = pre;
        while( next!=tailer&&soldierInfo.getPS()>next.getPS() ){
            pre = next;
            next = next.next;
        }
        while( pre!=header&&soldierInfo.getPS()<pre.getPS() ){
            next = pre;
            pre = pre.pre;
        }
        pre.next = soldierInfo;
        soldierInfo.pre = pre;
        soldierInfo.next = next;
        next.pre = soldierInfo;
    }

    private void addIntoList(SoldierInfo soldierInfo){
        if(header.next==tailer) {
            header.next = soldierInfo;
            soldierInfo.pre = header;
            soldierInfo.next = tailer;
            tailer.pre = soldierInfo;
        }else{
            SoldierInfo tmp1 = header;
            SoldierInfo tmp2 = header.next;
            while(tmp2!=tailer&&soldierInfo.getPS()>tmp2.getPS()){
                tmp1 = tmp2;
                tmp2 = tmp2.next;
            }
            tmp1.next = soldierInfo;
            soldierInfo.pre = tmp1;
            soldierInfo.next = tmp2;
            tmp2.pre = soldierInfo;
        }
    }

    public List<ReplicasInfo> getReplicas(int nums){
        if(nums>soldierInfoMap.size()) return null;
        List<ReplicasInfo> res = new ArrayList<>();
        SoldierInfo soldierInfo = header.next;
        for (int i=0;i<nums;i++){
            ReplicasInfo replicasInfo = new ReplicasInfo();
            replicasInfo.setTimeStamp(System.currentTimeMillis());
            replicasInfo.setStatus(-1);
            replicasInfo.setTPS(0f);
            replicasInfo.setQPS(0f);
            replicasInfo.setIp(soldierInfo.getIp());
            replicasInfo.setPort(soldierInfo.getPort());
            if(i==0) replicasInfo.setMaster(true);
            res.add(replicasInfo);
            soldierInfo = soldierInfo.next;
        }
        return res;
    }
}
