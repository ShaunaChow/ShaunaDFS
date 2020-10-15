package top.shauna.dfs.kingmanager.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 19:11
 * @E-Mail z1023778132@icloud.com
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckPoint implements Serializable {
    private Long uuid;
    private byte[] ShaunaImage;
    private byte[] editLog;
    private Integer status;
}
