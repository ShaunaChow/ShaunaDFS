package top.shauna.dfs.kingmanager;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.INode;
import top.shauna.dfs.kingmanager.bean.INodeDirectory;
import top.shauna.dfs.kingmanager.bean.INodeFile;
import top.shauna.dfs.starter.Starter;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 17:08
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ShaunaFSManager implements Starter {
    private INodeDirectory root;
    private static byte[] MAGIC_CODE = {62,02};
    private static byte FILE_FLAG = 0b01111111;
    private static byte DIR_FLAG = 0b1000000;

    @Override
    public void onStart() throws Exception {
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();
        String rootDir = kingPubConfig.getRootDir();
        File rootD = new File(rootDir+File.separator+"ShaunaImage.dat");
        loadRootNode(rootD);
    }

    private void loadRootNode(File file) throws Exception {
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        try{
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
        }finally {
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
                iNodes.add(readINode(in));
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

    public static void main(String[] args) throws Exception {
        ShaunaFSManager shaunaFSManager = new ShaunaFSManager();

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
    }
}
