package top.shauna.dfs.kingmanager.bean;

import lombok.*;
import top.shauna.dfs.interfaze.Writable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    @Override
    public boolean write(DataOutputStream fileOutputStream) throws IOException {
        byte[] name = getName().getBytes();
        fileOutputStream.writeByte(name.length);
        fileOutputStream.write(name);
        byte flag = 0;
        flag = (byte)(flag|0b1000000);
        fileOutputStream.writeByte(flag);
        fileOutputStream.writeByte(0);
        List<INode> children = getChildren();
        fileOutputStream.writeByte(children.size());
        fileOutputStream.flush();
        for (INode child : children) {
            child.write(fileOutputStream);
        }
        return true;
    }
}
