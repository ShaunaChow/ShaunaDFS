package top.shauna.dfs.kingmanager.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 16:10
 * @E-Mail z1023778132@icloud.com
 */
@Setter
@Getter
public class ReplicasInfo {
    private String ip;
    private String port;
    private Integer status;
    private Long timeStamp;
    private Boolean master;
    private Float QPS;
    private Float TPS;
}
