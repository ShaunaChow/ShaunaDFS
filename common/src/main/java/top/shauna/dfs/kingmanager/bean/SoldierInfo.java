package top.shauna.dfs.kingmanager.bean;

import lombok.*;

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
    private Long id;
    private String ip;
    private String port;
    private Boolean OK;
}
