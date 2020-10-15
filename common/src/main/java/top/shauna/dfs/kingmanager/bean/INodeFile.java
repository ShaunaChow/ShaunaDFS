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
public class INodeFile extends INode implements Serializable,Writable {
    private List<Block> blocks;

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean write(DataOutputStream fileOutputStream) throws IOException {
        fileOutputStream.writeInt(blocks.size());
        for (Block block : blocks) {
            block.write(fileOutputStream);
        }
        return true;
    }

    public static INodeFile load(DataInputStream fileInputStream) throws IOException {
        INodeFile iNodeFile = new INodeFile();
        int size = fileInputStream.readInt();
        ArrayList<Block> blocks = new ArrayList<>(size);
        for (int i=0;i<size;i++){
            blocks.add(Block.load(fileInputStream));
        }
        iNodeFile.setBlocks(blocks);
        return iNodeFile;
    }
}
