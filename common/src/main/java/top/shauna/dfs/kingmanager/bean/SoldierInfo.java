package top.shauna.dfs.kingmanager.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
import top.shauna.dfs.soldiermanager.bean.BlockInfo;

import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 16:16
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SoldierInfo {
    private Integer id;
    private String ip;
    private String port;
    private Boolean OK;
    private Long timeStamp;
    private Long freeSpace;
    @JSONField(serialize = false)
    private List<BlockInfo> blockInfos;
    @JSONField(serialize = false)
    private List<Transaction> transactions;
    private Float QPS;
    private Float TPS;
    private Long lastUsedTime;
    private Integer status;     /** 区分是养老代（-1）还是工作代（1） **/
    @JSONField(serialize = false)
    public SoldierInfo next;
    @JSONField(serialize = false)
    public SoldierInfo pre;

    public Float getPS(){
        return QPS+TPS;
    }
}
