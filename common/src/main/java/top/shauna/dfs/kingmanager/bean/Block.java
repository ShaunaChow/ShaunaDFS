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
 * @Date 2020/10/9 16:24
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Block implements Serializable,Writable {
    private String filePath;
    private Integer pin;
    private Long timeStamp;
    private Integer status;
    private Integer replicas;
    private List<ReplicasInfo> replicasInfos;
    private Integer blockLength;

    @Override
    public boolean write(DataOutputStream out) throws IOException {
        out.writeInt(blockLength);
        return true;
    }

    public static Block load(DataInputStream in) throws IOException {
        int length = in.readInt();
        Block block = new Block();
        block.setReplicas(0);
        block.setReplicasInfos(new CopyOnWriteArrayList<>());
        block.setBlockLength(length);
        return block;
    }

    public ReplicasInfo getReplocasInfo(int id){
        for (ReplicasInfo info : replicasInfos) {
            if(id==info.getId()) {
                return info;
            }
        }
        return null;
    }
}
