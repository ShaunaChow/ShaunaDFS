package top.shauna.dfs.kingmanager.bean;

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
    private String ip;
    private String port;
    private Boolean OK;
    private Long timeStamp;
    private List<BlockInfo> blockInfos;
    public SoldierInfo next;
    public SoldierInfo pre;

    public Float getPS(){
        if(blockInfos==null) return 0f;
        float sum = 0f;
        for (BlockInfo blockInfo : blockInfos) {
            sum += (blockInfo.getQPS()+blockInfo.getTPS());
        }
        return sum;
    }
}
