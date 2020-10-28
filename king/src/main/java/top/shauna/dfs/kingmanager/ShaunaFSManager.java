package top.shauna.dfs.kingmanager;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.config.KingPubConfig;
import top.shauna.dfs.kingmanager.bean.*;
import top.shauna.dfs.kingmanager.interfaze.FSManager;
import top.shauna.dfs.soldiermanager.bean.BlockInfo;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.storage.util.CheckPointUtil;
import top.shauna.dfs.threadpool.CommonThreadPool;
import top.shauna.dfs.type.ClientProtocolType;
import top.shauna.dfs.util.CommonUtil;
import top.shauna.dfs.util.KingUtils;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/9 17:08
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ShaunaFSManager implements Starter,FSManager {
    private INodeDirectory root;
    private ConcurrentHashMap<String,ClientFileInfo> newFileKeeper;
    private BlocksManager blocksManager;
    private static byte[] MAGIC_CODE = {62,02};
    private String rootDir;
    private CopyOnWriteArrayList<INode> deletedNode;

    public ShaunaFSManager(){
        newFileKeeper = new ConcurrentHashMap<>();
        blocksManager = BlocksManager.getInstance();
        KingPubConfig kingPubConfig = KingPubConfig.getInstance();
        rootDir = kingPubConfig.getRootDir();
        deletedNode = new CopyOnWriteArrayList<>();
    }

    @Override
    public void onStart() throws Exception {
        DataInputStream in = null;
        try{
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(rootDir+File.separator+"ShaunaImage.dat"))));
            root = CheckPointUtil.loadRootNode(in);
        }finally {
            if (in!=null){
                in.close();
            }
        }
        List<File> files = CommonUtil.scanEditLogFiles(KingPubConfig.getInstance().getEditLogDirs());
        if (files==null||files.size()==0){
            log.info("没有编辑日志文件");
        }else{
            for (File file : files) {
                DataInputStream editInput = null;
                try {
                    editInput = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                    List<LogItem> logItems = CheckPointUtil.loadEditLogs(editInput);
                    CheckPointUtil.mergeEditLogs(root,logItems);
                    editInput.close();
                    file.delete();
                }finally {
                    if (editInput!=null){
                        editInput.close();
                    }
                }
            }
        }

        initBlockManager();

        File oldImageFile = new File(rootDir+File.separator+"ShaunaImage.dat");
        File bakImageFile = new File(rootDir+File.separator+"ShaunaImage-bak.dat");
        oldImageFile.renameTo(bakImageFile);
        DataOutputStream rootOutput = null;
        try {
            rootOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(oldImageFile)));
            CheckPointUtil.saveRootNode(root,rootOutput);
        }finally {
            if (rootOutput!=null){
                rootOutput.close();
            }
        }
        bakImageFile.delete();

        log.info("KING的root节点初始化完成!!!");

        CommonThreadPool.threadPool.execute(()->{
            while (true){
                try {
                    doDelete();
                    log.info("执行删除完成!!!");
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doDelete() {
        for (int i=0;i<deletedNode.size();i++) {
            INode iNode = deletedNode.get(i);
            doDelete(iNode);
            deletedNode.remove(i--);
        }

        long now = System.currentTimeMillis();
        Integer uploadTime = KingPubConfig.getInstance().getFileOutOfTime();
        ConcurrentHashMap.KeySetView<String, ClientFileInfo> strings = newFileKeeper.keySet();
        for (String key : strings) {
            ClientFileInfo clientFileInfo = newFileKeeper.get(key);
            if (now-clientFileInfo.getTimeStamp()>=uploadTime*60*1000){
                INodeFile iNodeFile = clientFileInfo.getINodeFile();
                blocksManager.deleteBlocks(iNodeFile.getPath());
                newFileKeeper.remove(key);
            }
        }
    }

    private void doDelete(INode iNode){
        if (iNode instanceof INodeFile){
            INodeFile file = (INodeFile) iNode;
            blocksManager.deleteBlocks(file.getPath());
            INodeDirectory parent = (INodeDirectory) file.getParent();
            parent.getChildren().remove(file);
        }else{
            INodeDirectory directory = (INodeDirectory) iNode;
            INodeDirectory parent = (INodeDirectory) directory.getParent();
            parent.getChildren().remove(directory);
            for (INode node : directory.getChildren()) {
                doDelete(node);
            }
        }
    }

    private void initBlockManager(){
        for (INode iNode : root.getChildren()) {
            initBlockManager(iNode);
        }
    }

    private void initBlockManager(INode node){
        if (node instanceof INodeDirectory){
            INodeDirectory directory = (INodeDirectory) node;
            for (INode iNode : directory.getChildren()) {
                initBlockManager(iNode);
            }
        }else{
            INodeFile file = (INodeFile) node;
            blocksManager.registBlocks(file.getPath(),file.getBlocks());
        }
    }


    @Override
    public void uploadFile(ClientFileInfo fileInfo) throws Exception {
        String path = fileInfo.getPath();
        String dirPath = path.substring(0,1+path.lastIndexOf('/'));
        String fileName = fileInfo.getName();
        INodeDirectory directory = getINodeDirectory(dirPath);
        if (directory==null) fileInfo.setRes(ClientProtocolType.NO_SUCH_DIR);
        else{
            INodeFile newFile = null;
            List<INode> children = directory.getChildren();
            for (INode child : children) {
                if(child.getName().equals(fileName)){
                    if (child.getStatus()==null||child.getStatus()<0){
                        newFile = (INodeFile) child;
                        children.remove(child);
                        break;
                    }else {
                        fileInfo.setRes(ClientProtocolType.ALLREADY_EXITS);
                        return;
                    }
                }
            }
            if (newFile==null) newFile = new INodeFile();
            newFile.setName(fileName);
            newFile.setParent(directory);
            List<Block> blocks = blocksManager.requireBlocks(newFile.getPath(),fileInfo.getFileLength());
            KingPubConfig kingPubConfig = KingPubConfig.getInstance();
            SoldierManager soldierManager = SoldierManager.getInstance();
            for (Block block : blocks) {
                List<ReplicasInfo> replicasInfos = soldierManager.getReplicas(kingPubConfig.getReplicas(), kingPubConfig.getBlockSize());
                if (replicasInfos==null){
                    throw new Exception("Soldier不足副本要求数量");
                }else{
                    block.getReplicasInfos().addAll(replicasInfos);
                    block.setReplicas(block.getReplicasInfos().size());
                    block.setStatus(2);
                }
            }
            newFile.setBlocks(blocks);
            newFile.setStatus(2);      /** 设置状态码！！！！！2为新建 **/
            directory.getChildren().add(newFile);
            fileInfo.setINodeFile(newFile);
            blocksManager.registBlocks(newFile.getPath(),newFile.getBlocks());
            fileInfo.setRes(ClientProtocolType.SUCCESS);
            fileInfo.setTimeStamp(System.currentTimeMillis());
            newFileKeeper.put(path,fileInfo);
        }
    }

    @Override
    public void uploadFileOk(ClientFileInfo fileInfo) {
        if (newFileKeeper.containsKey(fileInfo.getPath())){
            ClientFileInfo clientFileInfo = newFileKeeper.get(fileInfo.getPath());
            clientFileInfo.getINodeFile().setStatus(1);     /** 设置状态码！！！！！ 1为正常文件**/
            SoldierManager soldierManager = SoldierManager.getInstance();
            for (Block block : clientFileInfo.getINodeFile().getBlocks()) {
                block.setStatus(1);
                for (ReplicasInfo replicasInfo : block.getReplicasInfos()) {
                    SoldierInfo soldierInfo = soldierManager.getSoldierInfo(replicasInfo.getId());
                    BlockInfo blockInfo = new BlockInfo();
                    blockInfo.setTPS(0F);
                    blockInfo.setQPS(0F);
                    blockInfo.setTimeStamp(System.currentTimeMillis());
                    blockInfo.setFilePath(block.getFilePath());
                    blockInfo.setPin(block.getPin());
                    soldierInfo.getBlockInfos().add(blockInfo);
                }
            }
            newFileKeeper.remove(fileInfo.getPath());
            fileInfo.setRes(ClientProtocolType.SUCCESS);
        }else{
            fileInfo.setRes(ClientProtocolType.OUT_OF_DATE);
        }
    }

    @Override
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
                        INodeFile file = (INodeFile) child;
                        if(file.getStatus()!=null&&file.getStatus()>=0) {
                            INodeFile nodeFile = (INodeFile) child;
                            resortReplicas(nodeFile);
                            fileInfo.setINodeFile(nodeFile);
                            fileInfo.setRes(ClientProtocolType.SUCCESS);
                        }else{
                            fileInfo.setRes(ClientProtocolType.NO_SUCH_File);
                        }
                    }else{
                        fileInfo.setRes(ClientProtocolType.UNKNOWN);
                    }
                    return;
                }
            }
            fileInfo.setRes(ClientProtocolType.NO_SUCH_File);
        }
    }

    private void resortReplicas(INodeFile nodeFile) {
        for (Block block : nodeFile.getBlocks()) {
            KingUtils.resortReplicas(block);
        }
    }

    private INodeDirectory getINodeDirectory(String dirPath) {
        if(dirPath.equals("/")) return root;
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
        if(chosed==null||chosed.getStatus()==null||chosed.getStatus()<0) return null;
        if(lastPath.equals("")) return (INodeDirectory)chosed;
        return getINodeDirectory((INodeDirectory)chosed,lastPath);
    }

    @Override
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
            String name = fileName.endsWith("/")?fileName:fileName+"/";
            for (INode iNode : directory.getChildren()) {
                if(iNode.getName().equals(name)){
                    if (iNode.getStatus()==null||iNode.getStatus()<0){
                        iNode.setStatus(1);
                        fileInfo.setRes(ClientProtocolType.SUCCESS);
                    }else{
                        fileInfo.setRes(ClientProtocolType.ALLREADY_EXITS);
                    }
                    return;
                }
            }
            INodeDirectory newDir = new INodeDirectory();
            newDir.setName(name);
            newDir.setChildren(new CopyOnWriteArrayList<>());
            newDir.setParent(directory);
            directory.getChildren().add(newDir);
            newDir.setStatus(1);
            fileInfo.setRes(ClientProtocolType.SUCCESS);
        }
    }

    @Override
    public void rmr(ClientFileInfo fileInfo, boolean rmAll) {
        String path = fileInfo.getPath();
        if(path.endsWith("/")){
            path = path.substring(0,path.length()-1);
        }
        String dirPath = path.substring(0,1+path.lastIndexOf('/'));
        String fileName = fileInfo.getName();
        INodeDirectory directory = getINodeDirectory(dirPath);
        if(directory==null) fileInfo.setRes(ClientProtocolType.NO_SUCH_DIR);
        else{
            List<INode> children = directory.getChildren();
            for (int i = 0; i< children.size(); i++) {
                INode node = children.get(i);
                if(node.getName().equals(fileName)){
                    if(node instanceof INodeDirectory) {        /** 删除目录 **/
                        INodeDirectory iNodeDirectory = (INodeDirectory) node;
                        if (iNodeDirectory.getChildren()==null||iNodeDirectory.getChildren().size()==0){
                            iNodeDirectory.setStatus(-1);
                            deletedNode.add(iNodeDirectory);
                            fileInfo.setRes(ClientProtocolType.SUCCESS);
                        }else if (rmAll){
                            setStatus(iNodeDirectory,-1,true);
                            deletedNode.add(iNodeDirectory);
                            fileInfo.setRes(ClientProtocolType.SUCCESS);
                        }else{
                            fileInfo.setRes(ClientProtocolType.IT_IS_NOT_AN_EMPTY_DIR);
                        }
                    }else {                                     /** 删除文件 **/
                        node.setStatus(-1);
                        deletedNode.add(node);
                        fileInfo.setRes(ClientProtocolType.SUCCESS);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void initFS() throws Exception {     /** 慎用 **/
        File rootD = new File(rootDir+File.separator+"ShaunaImage.dat");
        INodeDirectory initNode = new INodeDirectory();
        initNode.setParent(null);
        initNode.setStatus(1);
        initNode.setChildren(new CopyOnWriteArrayList<>());
        initNode.setName("/");
        initNode.setPath("");
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(rootD));
        outputStream.write(MAGIC_CODE);
        outputStream.write(new byte[]{0,0});
        outputStream.flush();
        CheckPointUtil.saveINode(initNode,outputStream);
    }

    private void setStatus(INode node, int status, boolean digui){
        if (node==null) return;
        node.setStatus(status);
        if (node instanceof INodeDirectory&&digui){
            INodeDirectory directory = (INodeDirectory) node;
            if (directory.getChildren()!=null) {
                for (INode iNode : directory.getChildren()) {
                    setStatus(iNode,status,digui);
                }
            }
        }
    }
}
