package top.shauna.dfs.kingmanager.bean;

import lombok.*;
import top.shauna.dfs.interfaze.Writable;
import top.shauna.dfs.type.ClientProtocolType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 15:15
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientFileInfo implements Serializable,Writable {
    private String path;
    private String name;
    private Long timeStamp;
    private Long fileLength;
    private INode iNode;
    private ClientProtocolType res;

    @Override
    public boolean write(DataOutputStream fileOutputStream) throws IOException {
        byte[] pathName = path.getBytes();
        fileOutputStream.writeInt(pathName.length);
        fileOutputStream.write(pathName);
        if (fileLength!=null) {
            fileOutputStream.writeByte(1);
            fileOutputStream.writeLong(fileLength);
        }else{
            fileOutputStream.writeByte(0);
        }
        if (iNode !=null) {
            fileOutputStream.writeByte(1);
            iNode.write(fileOutputStream);
        }else{
            fileOutputStream.writeByte(0);
        }
        return true;
    }

    public static ClientFileInfo load(DataInputStream fileInputStream) throws Exception {
        ClientFileInfo clientFileInfo = new ClientFileInfo();
        int nameLength = fileInputStream.readInt();
        byte[] nameBytes = new byte[nameLength];
        fileInputStream.read(nameBytes);
        clientFileInfo.setPath(new String(nameBytes));
        byte exitsLength = fileInputStream.readByte();
        if (exitsLength==1){
            clientFileInfo.setFileLength(fileInputStream.readLong());
        }
        byte exitsFile = fileInputStream.readByte();
        if (exitsFile==1){
            clientFileInfo.setINode((INodeFile) INodeFile.load(fileInputStream,null));
        }
        return clientFileInfo;
    }
}
