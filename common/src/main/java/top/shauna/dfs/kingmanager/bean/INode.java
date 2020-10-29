package top.shauna.dfs.kingmanager.bean;

import lombok.Getter;
import lombok.Setter;
import top.shauna.dfs.interfaze.Writable;

import java.io.DataInputStream;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 16:31
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
public abstract class INode implements Serializable,Writable {
    private INode parent;
    private String name;
    private String path;
    private Integer status;

    public abstract boolean isDirectory();

    public String getPath() {
        if(parent==null) return name;
        else return parent.getPath()+name;
    }

    public static INode load(DataInputStream in, INode father) throws Exception {
        int nameLen = (in.readByte()+256)%256;
        byte[] nameBytes = new byte[nameLen];
        in.read(nameBytes);
        byte flag = in.readByte();
        in.skipBytes(1);
        if((flag&0b1000000)!=0){ /** 目录 **/
            INodeDirectory directory = new INodeDirectory();
            directory.setName(new String(nameBytes));
            directory.setParent(father);
            int childNodes = (in.readByte() + 256) % 256;
            CopyOnWriteArrayList<INode> iNodes = new CopyOnWriteArrayList<>();
            for(int i=0;i<childNodes;i++){
                INode iNode = load(in,directory);
                iNodes.add(iNode);
            }
            directory.setChildren(iNodes);
            directory.setStatus(1);
            return directory;
        }else{                  /** 文件 **/
            INodeFile file = new INodeFile();
            file.setName(new String(nameBytes));
            file.setParent(father);
            int blocks = (in.readByte() + 256) % 256;
            CopyOnWriteArrayList<Block> blockList = new CopyOnWriteArrayList<>();
            for(int i=0;i<blocks;i++){
                Block load = Block.load(in);
                blockList.add(load);
            }
            file.setBlocks(blockList);
            file.setStatus(1);
            return file;
        }
    }
}
