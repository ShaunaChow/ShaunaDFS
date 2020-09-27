package top.shauna.dfs.bean;

import lombok.*;

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
public class HeartBeatResponseBean {
    private String ip;
    private String port;
    private Long timeStamp;
    private List<BlockInfo> blockInfos;
}
