package top.shauna.dfs.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 20:46
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaInfo {
    private String filePath;
    private Integer pin;
    private Long version;
    private String metaPath;
    private DataInfo dataInfo;
}
