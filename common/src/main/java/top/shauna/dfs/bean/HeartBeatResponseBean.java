package top.shauna.dfs.bean;

import lombok.*;
import top.shauna.dfs.kingmanager.bean.Transaction;
import top.shauna.dfs.soldiermanager.bean.BlockInfo;
import top.shauna.dfs.type.HeartBeatResponseType;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 20:48
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HeartBeatResponseBean implements Serializable {
    private Integer id;
    private String ip;
    private String port;
    private Long timeStamp;
    private List<BlockInfo> blockInfos;
    private HeartBeatResponseType res;
    private List<Transaction> transactions;
}
