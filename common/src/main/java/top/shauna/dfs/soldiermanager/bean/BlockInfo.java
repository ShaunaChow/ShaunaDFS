package top.shauna.dfs.soldiermanager.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.shauna.dfs.type.HeartBeatResponseType;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 20:51
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockInfo implements Serializable {
    private String filePath;
    private Integer pin;
    private Long timeStamp;
    private Float QPS;
    private Float TPS;
    private HeartBeatResponseType res;
    private String msg;
}
