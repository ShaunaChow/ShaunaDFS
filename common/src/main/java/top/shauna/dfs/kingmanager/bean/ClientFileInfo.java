package top.shauna.dfs.kingmanager.bean;

import lombok.*;
import top.shauna.dfs.type.ClientProtocolType;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 15:15
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientFileInfo implements Serializable {
    private String path;
    private String name;
    private Long fileLength;
    private INodeFile iNodeFile;
    private ClientProtocolType res;
}
