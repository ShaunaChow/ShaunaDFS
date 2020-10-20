package top.shauna.dfs.kingmanager.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 16:10
 * @E-Mail z1023778132@icloud.com
 */
@Setter
@Getter
public class ReplicasInfo implements Serializable {
    private Integer id;
    private String ip;
    private String port;
    private Integer status;
    private Long timeStamp;
    private Boolean master;
    private Float QPS;
    private Float TPS;

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (obj==null) return false;
        if (obj instanceof ReplicasInfo){
            ReplicasInfo replicasInfo = (ReplicasInfo) obj;
            if (replicasInfo.getIp().equals(this.ip)&&replicasInfo.getPort().equals(this.port)){
                return true;
            }
        }
        return false;
    }
}
