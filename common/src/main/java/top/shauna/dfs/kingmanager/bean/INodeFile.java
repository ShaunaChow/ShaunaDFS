package top.shauna.dfs.kingmanager.bean;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 16:32
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class INodeFile extends INode implements Serializable {
    private List<Block> blocks;

    @Override
    public boolean isDirectory() {
        return false;
    }
}
