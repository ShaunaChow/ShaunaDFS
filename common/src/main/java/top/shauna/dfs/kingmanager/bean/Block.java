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
    private Boolean Ok;
    private Integer replicas;
    private List<ReplicasInfo> replicasInfos;
    private Integer blockLength;

    @Override
    public boolean write(DataOutputStream out) throws IOException {
        out.writeByte(replicas);
        for (ReplicasInfo soldierInfo : replicasInfos) {
            String ip = soldierInfo.getIp();
            for (String s : ip.split("\\.")) {
                out.writeByte(Integer.parseInt(s));
            }
            String port = soldierInfo.getPort();
            out.writeShort(Integer.parseInt(port));
        }
        return true;
    }

    public static Block load(DataInputStream in) throws IOException {
        int replicaNums = (in.readByte()+256)%256;
        List<ReplicasInfo> soldiers = new ArrayList<>();
        for (int i = 0; i < replicaNums; i++) {
            ReplicasInfo soldier = new ReplicasInfo();
            byte[] ip = new byte[4];
            in.read(ip);
            soldier.setIp(getIP(ip));
            int port = (in.readShort()+65536)%65536;
            soldier.setPort(String.valueOf(port));
            soldier.setStatus(-1);
            soldiers.add(soldier);
        }
        Block block = new Block();
        block.setReplicas(replicaNums);
        block.setReplicasInfos(soldiers);
        return block;
    }

    private static String getIP(byte[] ip_port){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<4;i++){
            int num = (ip_port[i] + 256) % 256;
            if (i!=3){
                sb.append(num).append(".");
            }else{
                sb.append(num);
            }
        }
        return sb.toString();
    }

    public ReplicasInfo getReplocasInfo(String ip, String port){
        for (ReplicasInfo info : replicasInfos) {
            if(ip.equals(info.getIp())&&port.equals(info.getPort())) {
                return info;
            }
        }
        return null;
    }

    public ReplicasInfo getMaster(){
        return replicasInfos.get(0);
    }
}
