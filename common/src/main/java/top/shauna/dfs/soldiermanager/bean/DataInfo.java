package top.shauna.dfs.soldiermanager.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 20:17
 * @E-Mail z1023778132@icloud.com
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataInfo implements Serializable {
    private String dataPath;
    private String md5;
    private byte[] content;
}
