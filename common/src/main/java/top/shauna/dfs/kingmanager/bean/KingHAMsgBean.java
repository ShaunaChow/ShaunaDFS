package top.shauna.dfs.kingmanager.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.shauna.dfs.type.KingHAMsgType;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 20:55
 * @E-Mail z1023778132@icloud.com
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KingHAMsgBean implements Serializable {
    private String ip_port;
    private Long id;
    private KingHAMsgType msg;
    private CheckPoint checkPoint;
}
