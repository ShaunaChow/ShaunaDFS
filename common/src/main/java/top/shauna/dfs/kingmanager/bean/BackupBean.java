package top.shauna.dfs.kingmanager.bean;

import lombok.*;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/19 20:35
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BackupBean implements Serializable {
    private String filePath;
    private Integer pin;
    private ReplicasInfo goodReplicas;
    private ReplicasInfo newReplicas;
}
