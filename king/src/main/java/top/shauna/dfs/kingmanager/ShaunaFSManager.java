package top.shauna.dfs.kingmanager;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.interact.client.impl.ClientProtocolImpl;
import top.shauna.dfs.kingmanager.bean.*;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.type.ClientProtocolType;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 17:08
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ShaunaFSManager implements Starter {
    private INodeDirectory root;
    private ConcurrentHashMap<String,ClientFileInfo> newFileKeeper;
    private static byte[] MAGIC_CODE = {62,02};
    private static byte FILE_FLAG = 0b01111111;
    private static byte DIR_FLAG = 0b1000000;

    private static volatile ShaunaFSManager shaunaFSManager;

    private ShaunaFSManager(){
        newFileKeeper = new ConcurrentHashMap<>();
    }

    public static ShaunaFSManager getInstance(){
        if(shaunaFSManager==null){
            synchronized (ShaunaFSManager.class){
                if(shaunaFSManager==null){
                    shaunaFSManager = new ShaunaFSManager();
                }
            }
        }
        return shaunaFSManager;
    }

    @Override
    public void onStart() throws Exception {
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();
        String rootDir = kingPubConfig.getRootDir();
        File rootD = new File(rootDir+File.separator+"ShaunaImage.dat");
        loadRootNode(rootD);
    }

    private void loadRootNode(File file) throws Exception {
        DataInputStream in = null;
        try{
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            byte magic1 = in.readByte();
            byte magic2 = in.readByte();
            if(magic1!=MAGIC_CODE[0]||magic2!=MAGIC_CODE[1]) {
                log.error("魔术不匹配");
                throw new Exception("魔术不匹配");
            }
            in.skipBytes(2);
            root = (INodeDirectory) readINode(in);
            root.setName("/");
            root.setPath("");
        } catch (FileNotFoundException e) {
            if(root==null) root = new INodeDirectory();
            root.setChildren(new CopyOnWriteArrayList<>());
            root.setName("/");
            root.setPath("");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private INode readINode(DataInputStream in) throws Exception {
        int nameLen = (in.readByte()+256)%256;
        byte[] nameBytes = new byte[nameLen];
        in.read(nameBytes);
        byte flag = in.readByte();
        in.skipBytes(1);
        if((flag&DIR_FLAG)!=0){ /** 目录 **/
            INodeDirectory directory = new INodeDirectory();
            directory.setName(new String(nameBytes));
            int childNodes = (in.readByte() + 256) % 256;
            CopyOnWriteArrayList<INode> iNodes = new CopyOnWriteArrayList<>();
            for(int i=0;i<childNodes;i++){
                INode iNode = readINode(in);
                iNode.setParent(directory);
                iNodes.add(iNode);
            }
            directory.setChildren(iNodes);
            return directory;
        }else{                  /** 文件 **/
            INodeFile file = new INodeFile();
            file.setName(new String(nameBytes));
            int blocks = (in.readByte() + 256) % 256;
            CopyOnWriteArrayList<Block> blockList = new CopyOnWriteArrayList<>();
            for(int i=0;i<blocks;i++){
                blockList.add(Block.load(in));
            }
            file.setBlocks(blockList);
            return file;
        }
    }

    private void saveRootNode(File newFile) throws Exception {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newFile)));
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
            out.writeByte(children.size());
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
            out.writeByte(blocks.size());
            for (Block block : blocks) {
                block.write(out);
            }
            out.flush();
        }
    }

    public void uploadFile(ClientFileInfo fileInfo){
        String path = fileInfo.getPath();
        String dirPath = path.substring(0,1+path.lastIndexOf('/'));
        String fileName = fileInfo.getName();
        INodeDirectory directory = getINodeDirectory(dirPath);
        if (directory==null) fileInfo.setRes(ClientProtocolType.NO_SUCH_DIR);
        else{
            List<INode> children = directory.getChildren();
            for (INode child : children) {
                if(child.getName().equals(fileName)){
                    fileInfo.setRes(ClientProtocolType.ALLREADY_EXITS);
                    return;
                }
            }
            INodeFile newFile = new INodeFile();
            newFile.setName(fileName);
            newFile.setParent(directory);
            List<Block> blocks = BlocksManager.getInstance().getBlocks(fileInfo.getFileLength());
            newFile.setBlocks(blocks);
            newFile.setStatus(-1);      /** 设置状态码！！！！！ **/
            directory.getChildren().add(newFile);
            fileInfo.setINodeFile(newFile);
            fileInfo.setRes(ClientProtocolType.SUCCESS);
            newFileKeeper.put(path,fileInfo);
        }
    }

    public void uploadFileOk(ClientFileInfo fileInfo) {
        if (newFileKeeper.containsKey(fileInfo.getPath())){
            ClientFileInfo clientFileInfo = newFileKeeper.get(fileInfo.getPath());
            clientFileInfo.getINodeFile().setStatus(1);     /** 设置状态码！！！！！ **/
            newFileKeeper.remove(fileInfo.getPath());
        }
    }

    public void downloadFile(ClientFileInfo fileInfo){
        String path = fileInfo.getPath();
        String dirPath = path.substring(0,1+path.lastIndexOf('/'));
        String fileName = fileInfo.getName();
        INodeDirectory directory = getINodeDirectory(dirPath);
        if (directory==null) fileInfo.setRes(ClientProtocolType.NO_SUCH_DIR);
        else{
            List<INode> children = directory.getChildren();
            for (INode child : children) {
                if(child.getName().equals(fileName)){
                    if(child instanceof INodeFile){
                        fileInfo.setINodeFile((INodeFile) child);
                        fileInfo.setRes(ClientProtocolType.SUCCESS);
                    }else{
                        fileInfo.setRes(ClientProtocolType.UNKNOWN);
                    }
                    return;
                }
            }
            fileInfo.setRes(ClientProtocolType.NO_SUCH_File);
        }
    }

    private INodeDirectory getINodeDirectory(String dirPath) {
        if (!dirPath.startsWith("/")||!dirPath.endsWith("/")){
            log.error("路径地址无效"+dirPath);
            return null;
        }
        String curName = dirPath.substring(1);
        return getINodeDirectory(root, curName);
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
        if(chosed==null) return null;
        if(lastPath.equals("")) return (INodeDirectory)chosed;
        return getINodeDirectory((INodeDirectory)chosed,lastPath);
    }


    public void mkdir(ClientFileInfo fileInfo) {
        String path = fileInfo.getPath();
        if(path.endsWith("/")){
            path = path.substring(0,path.length()-1);
        }
        String dirPath = path.substring(0,1+path.lastIndexOf('/'));
        String fileName = fileInfo.getName();
        INodeDirectory directory = getINodeDirectory(dirPath);
        if(directory==null) fileInfo.setRes(ClientProtocolType.NO_SUCH_DIR);
        else{
            INodeDirectory newDir = new INodeDirectory();
            newDir.setName(fileName.endsWith("/")?fileName:fileName+"/");
            newDir.setChildren(new CopyOnWriteArrayList<>());
            newDir.setParent(directory);
            directory.getChildren().add(newDir);
            fileInfo.setRes(ClientProtocolType.SUCCESS);
        }
    }

    public static void main(String[] args) throws Exception {
        ShaunaFSManager shaunaFSManager = ShaunaFSManager.getInstance();

        INodeDirectory root = new INodeDirectory();
        root.setName("/");

        INodeFile file1 = new INodeFile();
        file1.setBlocks(new CopyOnWriteArrayList<>());
        file1.setName("file1");

        INodeDirectory directory = new INodeDirectory();
        directory.setName("ok");
        CopyOnWriteArrayList<INode> childList = new CopyOnWriteArrayList<>();
        INodeFile file2 = new INodeFile();
        file2.setBlocks(new CopyOnWriteArrayList<>());
        file2.setName("file2");
        childList.add(file2);
        directory.setChildren(childList);

        CopyOnWriteArrayList<INode> rootlist = new CopyOnWriteArrayList<>();
        rootlist.add(file1);
        rootlist.add(directory);
        root.setChildren(rootlist);

        shaunaFSManager.root = root;
        shaunaFSManager.saveRootNode(new File("F:\\java项目\\shauna.txt"));

        shaunaFSManager.root = null;
        shaunaFSManager.loadRootNode(new File("F:\\java项目\\shauna.txt"));
        INodeDirectory root1 = shaunaFSManager.root;
        List<INode> children = root1.getChildren();
        INode iNode = ((INodeDirectory)children.get(1)).getChildren().get(0);
        System.out.println(iNode.getPath());

        PubConfig pubConfig = PubConfig.getInstance();
        if (pubConfig.getRegisterBean()==null) {
            RegisterBean registerBean = new RegisterBean("zookeeper","39.105.89.185:2181",null);
            pubConfig.setRegisterBean(registerBean);
        }
        if (pubConfig.getFoundBean()==null) {
            RegisterBean registerBean = pubConfig.getRegisterBean();
            FoundBean foundBean = new FoundBean(
                    registerBean.getPotocol(),
                    registerBean.getUrl(),
                    registerBean.getLoc()
            );
            pubConfig.setFoundBean(foundBean);
        }

        LocalExportBean localExportBean = new LocalExportBean("netty", 9009, "127.0.0.1");
        ShaunaRPCHandler.publishServiceBean(ClientProtocol.class,new ClientProtocolImpl(),localExportBean);

        System.in.read();
    }

}
