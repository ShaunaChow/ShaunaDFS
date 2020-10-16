package top.shauna.dfs.kingmanager.bean;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.interfaze.Writable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/14 20:18
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class LogItem implements Serializable,Writable {
    private String method;
    private ClientFileInfo clientFileInfo;
    private Integer status;

    @Override
    public boolean write(DataOutputStream fileOutputStream) throws IOException {
        if(method.equalsIgnoreCase("uploadFile")){
            byte _1st = 1<<2;
            _1st |= getStatusCode();
            fileOutputStream.writeByte(_1st);
            if (status<0) {
                clientFileInfo.write(fileOutputStream);
            }else{
                writePath(fileOutputStream);
            }
        }else if(method.equalsIgnoreCase("mkdir")){
            byte _1st = 2<<2;
            _1st |= getStatusCode();
            fileOutputStream.writeByte(_1st);
            writePath(fileOutputStream);
        }else if(method.equalsIgnoreCase("rmr")){
            byte _1st = 3<<2;
            _1st |= getStatusCode();
            fileOutputStream.writeByte(_1st);
            writePath(fileOutputStream);
        }else{
            log.info("占不支持的类型");
            return false;
        }
        fileOutputStream.flush();
        return true;
    }

    public static LogItem load(DataInputStream fileinputStream) throws IOException {
        LogItem logItem = new LogItem();
        byte _1st = fileinputStream.readByte();
        Integer statusCode = getStatusCode(_1st);
        logItem.setStatus(statusCode);
        if ((_1st>>>2)==1){
            logItem.setMethod("uploadFile");
            if (statusCode<0) {
                logItem.setClientFileInfo(ClientFileInfo.load(fileinputStream));
            }else{
                ClientFileInfo fileInfo = new ClientFileInfo();
                fileInfo.setPath(readPath(fileinputStream));
                logItem.setClientFileInfo(fileInfo);
            }
        }else if((_1st>>>2)==2){
            logItem.setMethod("mkdir");
            ClientFileInfo fileInfo = new ClientFileInfo();
            fileInfo.setPath(readPath(fileinputStream));
            logItem.setClientFileInfo(fileInfo);
        }else if((_1st>>>2)==3){
            logItem.setMethod("rmr");
            ClientFileInfo fileInfo = new ClientFileInfo();
            fileInfo.setPath(readPath(fileinputStream));
            logItem.setClientFileInfo(fileInfo);
        }else{
            return null;
        }
        return logItem;
    }

    private void writePath(DataOutputStream fileOutputStream) throws IOException {
        String path = clientFileInfo.getPath();
        byte[] bytes = path.getBytes();
        fileOutputStream.writeInt(bytes.length);
        fileOutputStream.write(bytes);
    }

    private static String readPath(DataInputStream dataInputStream) throws IOException {
        int len = dataInputStream.readInt();
        byte[] path = new byte[len];
        dataInputStream.read(path);
        return new String(path);
    }

    private byte getStatusCode(){
        if (status==null){
            return 0b11;
        }else if(status>0){
            return 0b01;
        }else if(status<0){
            return 0b00;
        }else {
            return 0b10;
        }
    }

    private static Integer getStatusCode(byte b){
        byte a = 3;
        byte tar = (byte)(b&a);
        if (tar==3) {
            return null;
        }else if(tar==1){
            return 1;
        }else if(tar==0){
            return -1;
        }else{
            return 0;
        }
    }
}
