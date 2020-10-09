package top.shauna.dfs.kingmanager.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 16:31
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
public abstract class INode {
    protected INode parent;
    protected String name;

    public abstract boolean isDirectory();
}
