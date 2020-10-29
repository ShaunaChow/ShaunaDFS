package top.shauna.dfs.kingmanager.bean;

import lombok.*;
import top.shauna.dfs.interfaze.Writable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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

    @Override
    public boolean write(DataOutputStream fileOutputStream) throws IOException {
        byte[] name = getName().getBytes();
        fileOutputStream.writeByte(name.length);
        fileOutputStream.write(name);
        byte flag = 0;
        flag = (byte)(flag&0b01111111);
        fileOutputStream.writeByte(flag);
        fileOutputStream.writeByte(0);
        List<Block> blocks = getBlocks();
        if (blocks==null){
            fileOutputStream.writeByte(0);
        }else{
            fileOutputStream.writeByte(blocks.size());
            for (Block block : blocks) {
                block.write(fileOutputStream);
            }
        }
        fileOutputStream.flush();
        return true;
    }
}
