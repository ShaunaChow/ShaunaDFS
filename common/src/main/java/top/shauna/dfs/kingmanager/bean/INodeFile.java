package top.shauna.dfs.kingmanager.bean;

import lombok.*;

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
public class INodeFile extends INode {
    private List<Block> blocks;
    private Integer status;

    @Override
    public boolean isDirectory() {
        return false;
    }
}