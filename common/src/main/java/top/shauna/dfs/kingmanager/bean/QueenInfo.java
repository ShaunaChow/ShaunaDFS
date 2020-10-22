package top.shauna.dfs.kingmanager.bean;

import lombok.*;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/22 17:03
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueenInfo implements Serializable {
    private Integer id;
    private String ip;
    private String port;
    private Boolean OK;
    private Long timeStamp;
    private Long freeSpace;
    private QueenInfo masterQueen;
    private Boolean needCheck;
}
