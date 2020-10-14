package top.shauna.dfs.kingmanager.bean;

import lombok.*;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/14 20:18
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LogItem implements Serializable {
    private String method;
    private ClientFileInfo clientFileInfo;
    private Integer status;
}
