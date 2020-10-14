package top.shauna.dfs.soldiermanager.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 15:49
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Block implements Serializable {
    private String uuid;
    private String md5;
    private Long version;
    private String filePath;
    private Integer pin;
    private byte[] content;
    private List<ReplicasInfo> replicasInfos;
}
