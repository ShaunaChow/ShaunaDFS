package top.shauna.dfs.storage.util;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.kingmanager.bean.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/16 20:04
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class CheckPointUtil {
    private static byte[] MAGIC_CODE = {62,02};

    public static void mergeEditLogs(INodeDirectory root, List<LogItem> editLogs) {
        for (LogItem editLog : editLogs) {
            if (editLog.getStatus()==null||editLog.getStatus()<0) {
                continue;
            }
            ClientFileInfo fileInfo = editLog.getClientFileInfo();
            String path = fileInfo.getPath();
            if (editLog.getMethod().equalsIgnoreCase("uploadFile")){
                if (path.endsWith("/")) {
                    path = path.substring(0,path.length()-1);
                }
                String name = path.substring(path.lastIndexOf('/')+1);
                path = path.substring(0,path.lastIndexOf('/')+1);
                INodeDirectory father;
                if (path.equals("/")) {
                    father = root;
                }else{
                    path = path.substring(1);
                    father = getINodeDirectory(root,path);
                }
                List<INode> children = father.getChildren();
                INodeFile newFile = null;
                for (INode child : children) {
                    if (child.getName().equals(name)) {
                        newFile = (INodeFile) child;
                        newFile.setStatus(1);
                        break;
                    }
                }
                if (newFile!=null){
                    continue;
                }
                newFile = fileInfo.getINodeFile();
                newFile.setParent(father);
                newFile.setStatus(1);
                newFile.setName(name);
                father.getChildren().add(newFile);
            }else if (editLog.getMethod().equalsIgnoreCase("mkdir")){
                if (path.endsWith("/")) {
                    path = path.substring(0,path.length()-1);
                }
                String name = path.substring(path.lastIndexOf('/')+1)+"/";
                path = path.substring(0,path.lastIndexOf('/')+1);
                INodeDirectory father = null;
                if (path.equals("/")) {
                    father = root;
                }else{
                    path = path.substring(1);
                    father = getINodeDirectory(root,path);
                }
                List<INode> children = father.getChildren();
                INodeDirectory directory = null;
                for (INode child : children) {
                    if (child.getName().equals(name)) {
                        directory = (INodeDirectory) child;
                        directory.setStatus(1);
                        break;
                    }
                }
                if (directory!=null) {
                    continue;
                }
                directory = new INodeDirectory();
                directory.setName(name);
                directory.setChildren(new CopyOnWriteArrayList<>());
                directory.setStatus(1);
                directory.setParent(father);
                father.getChildren().add(directory);
            }else if (editLog.getMethod().equalsIgnoreCase("rmr")){
                String name ;
                if (path.endsWith("/")) {
                    path = path.substring(0,path.length()-1);
                    name = path.substring(path.lastIndexOf('/')+1)+"/";
                }else{
                    name = path.substring(path.lastIndexOf('/')+1);
                }
                path = path.substring(0,path.lastIndexOf('/')+1);
                INodeDirectory father = null;
                if (path.equals("/")) {
                    father = root;
                }else{
                    path = path.substring(1);
                    father = getINodeDirectory(root,path);
                }
                List<INode> children = father.getChildren();
                for (INode child : children) {
                    if (child.getName().equals(name)) {
                        child.setParent(null);
                        children.remove(child);
                        break;
                    }
                }
            }
        }
    }

    public static INodeDirectory getINodeDirectory(INodeDirectory directory, String curName){
        String dirName = curName.substring(0,curName.indexOf('/')+1);
        String lastPath = curName.substring(curName.indexOf('/')+1);
        List<INode> children = directory.getChildren();
        INode chosed = null;
        for (INode child : children) {
            if (child.getName().equals(dirName)) {
                chosed = child;
                break;
            }
        }
        if(chosed==null) return null;
        if(lastPath.equals("")) return (INodeDirectory)chosed;
        return getINodeDirectory((INodeDirectory)chosed,lastPath);
    }

    public static List<LogItem> loadEditLogs(DataInputStream logInput) throws Exception {
        List<LogItem> res = new ArrayList<>();
        while (logInput.available()>0){
            LogItem logItem = LogItem.load(logInput);
            if (logItem!=null) {
                res.add(logItem);
            }
        }
        return res;
    }

    public static INodeDirectory loadRootNode(DataInputStream in) throws Exception {
        INodeDirectory root;
        try{
            byte magic1 = in.readByte();
            byte magic2 = in.readByte();
            if(magic1!=MAGIC_CODE[0]||magic2!=MAGIC_CODE[1]) {
                log.error("魔术不匹配");
                throw new Exception("魔术不匹配");
            }
            in.skipBytes(2);
            root = (INodeDirectory) INode.load(in,null);
            root.setName("/");
            root.setPath("");
            return root;
        } catch (FileNotFoundException e) {
            log.error("解析root出错！！！");
            return null;
        }
    }

    public static byte[] saveRootNode(INodeDirectory root) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        saveRootNode(root,out);
        return bytes.toByteArray();
    }

    public static void saveRootNode(INodeDirectory root, DataOutputStream out) throws Exception {
        try{
            out.write(MAGIC_CODE);
            out.write(new byte[]{0,0});
            out.flush();
            root.write(out);
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
