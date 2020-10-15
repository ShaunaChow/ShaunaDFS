package top.shauna.dfs.checkpoint;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.kingmanager.bean.*;
import top.shauna.dfs.protocol.QueenProtocol;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 20:26
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class DealWithCheckPoint {
    private QueenProtocol queenProtocol;
    private static byte[] MAGIC_CODE = {62,02};
    private static byte FILE_FLAG = 0b01111111;
    private static byte DIR_FLAG = 0b1000000;

    public DealWithCheckPoint(){
        queenProtocol = ShaunaRPCHandler.getReferenceProxy(QueenProtocol.class);
    }

    public void doCheckPoint() throws Exception {
        if (queenProtocol.needCheckPoint()){
            CheckPoint checkPoint = queenProtocol.doCheckPoint();
            if(checkPoint==null||checkPoint.getStatus()<0) {
                log.error("KING回复的CheckPoint状态出错");
                return;
            }
            byte[] image = checkPoint.getShaunaImage();
            DataInputStream imageInput = new DataInputStream(new ByteArrayInputStream(image));
            byte[] editLog = checkPoint.getEditLog();
            DataInputStream logInput = new DataInputStream(new ByteArrayInputStream(editLog));
            INodeDirectory root = loadRootNode(imageInput);
            List<LogItem> editLogs = loadEditLogs(logInput);
            mergeEditLogs(root,editLogs);
            byte[] newImage = saveRootNode(root);
            CheckPoint newCheckPoint = new CheckPoint(checkPoint.getUuid(), newImage, null, 1);
            queenProtocol.checkPointOk(newCheckPoint);
        }
    }

    private byte[] saveRootNode(INodeDirectory root) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        try{
            out.write(MAGIC_CODE);
            out.write(new byte[]{0,0});
            out.flush();
            saveINode(root,out);
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes.toByteArray();
    }

    private void saveINode(INode node, DataOutputStream out) throws Exception {
        if(node.isDirectory()){
            INodeDirectory dirNode = (INodeDirectory) node;
            byte[] name = dirNode.getName().getBytes();
            out.writeByte(name.length);
            out.write(name);
            byte flag = 0;
            flag = (byte)(flag|DIR_FLAG);
            out.writeByte(flag);
            out.writeByte(0);
            List<INode> children = dirNode.getChildren();
            int nums = 0;
            for (INode child : children) {
                nums++;
            }
            out.writeByte(nums);
            out.flush();
            for (INode child : children) {
                saveINode(child,out);
            }
        }else{
            INodeFile fileNode = (INodeFile) node;
            byte[] name = fileNode.getName().getBytes();
            out.writeByte(name.length);
            out.write(name);
            byte flag = 0;
            flag = (byte)(flag&FILE_FLAG);
            out.writeByte(flag);
            out.writeByte(0);
            List<Block> blocks = fileNode.getBlocks();
            if (blocks==null){
                out.writeByte(0);
            }else{
                out.writeByte(blocks.size());
                for (Block block : blocks) {
                    block.write(out);
                }
            }
        }
        out.flush();
    }

    private void mergeEditLogs(INodeDirectory root, List<LogItem> editLogs) {
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
                INodeDirectory father = null;
                if (path.equals("/")) {
                    father = root;
                }else{
                    path = path.substring(1);
                    father = getINodeDirectory(root,path);
                }
                INodeFile newFile = fileInfo.getINodeFile();
                newFile.setParent(father);
                newFile.setStatus(-1);
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
                INodeDirectory directory = new INodeDirectory();
                directory.setName(name);
                directory.setChildren(new CopyOnWriteArrayList<>());
                directory.setStatus(-1);
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

    private INodeDirectory getINodeDirectory(INodeDirectory directory, String curName){
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
        if(chosed==null||chosed.getStatus()==null) return null;
        if(lastPath.equals("")) return (INodeDirectory)chosed;
        return getINodeDirectory((INodeDirectory)chosed,lastPath);
    }

    private List<LogItem> loadEditLogs(DataInputStream logInput) throws IOException {
        List<LogItem> res = new ArrayList<>();
        while (logInput.available()>0){
            res.add(LogItem.load(logInput));
        }
        return res;
    }

    private INodeDirectory loadRootNode(DataInputStream in) throws Exception {
        INodeDirectory root;
        try{
            byte magic1 = in.readByte();
            byte magic2 = in.readByte();
            if(magic1!=MAGIC_CODE[0]||magic2!=MAGIC_CODE[1]) {
                log.error("魔术不匹配");
                throw new Exception("魔术不匹配");
            }
            in.skipBytes(2);
            root = (INodeDirectory) readINode(in,null);
            root.setName("/");
            root.setPath("");
            return root;
        } catch (FileNotFoundException e) {
            log.error("解析root出错！！！");
            return null;
        }
    }

    private INode readINode(DataInputStream in, INode father) throws Exception {
        int nameLen = (in.readByte()+256)%256;
        byte[] nameBytes = new byte[nameLen];
        in.read(nameBytes);
        byte flag = in.readByte();
        in.skipBytes(1);
        if((flag&DIR_FLAG)!=0){ /** 目录 **/
            INodeDirectory directory = new INodeDirectory();
            directory.setName(new String(nameBytes));
            directory.setParent(father);
            int childNodes = (in.readByte() + 256) % 256;
            CopyOnWriteArrayList<INode> iNodes = new CopyOnWriteArrayList<>();
            for(int i=0;i<childNodes;i++){
                INode iNode = readINode(in,directory);
                iNodes.add(iNode);
            }
            directory.setChildren(iNodes);
            return directory;
        }else{                  /** 文件 **/
            INodeFile file = new INodeFile();
            file.setName(new String(nameBytes));
            file.setParent(father);
            int blocks = (in.readByte() + 256) % 256;
            CopyOnWriteArrayList<Block> blockList = new CopyOnWriteArrayList<>();
            for(int i=0;i<blocks;i++){
                Block load = Block.load(in);
                load.setPin(i);
                blockList.add(load);
            }
            file.setBlocks(blockList);
            return file;
        }
    }

}
