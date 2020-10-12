package top.shauna.dfs.kingmanager.bean;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 16:38
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class INodeDirectory extends INode implements Serializable {
    private List<INode> children;

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public void setName(String name) {
        if(!name.endsWith("/")){
            super.setName(name+"/");
        }else{
            super.setName(name);
        }
    }
}
