package top.shauna.dfs.service.impl;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.kingmanager.bean.Block;
import top.shauna.dfs.kingmanager.bean.ClientFileInfo;
import top.shauna.dfs.kingmanager.bean.INodeFile;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.dfs.protocol.SoldierServerProtocol;
import top.shauna.dfs.service.ClientService;
import top.shauna.dfs.soldiermanager.bean.SoldierResponse;
import top.shauna.dfs.util.CommonUtil;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 21:55
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ClientServiceImpl implements ClientService {
    private ClientProtocol clientProtocol;
    private ConcurrentHashMap<String,SoldierServerProtocol> connectKeeper;

    public ClientServiceImpl(){
        preparePubConfig();
        clientProtocol = ShaunaRPCHandler.getReferenceProxy(ClientProtocol.class);
        connectKeeper = new ConcurrentHashMap<>();
    }

    private void preparePubConfig() {
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
    }

    @Override
    public boolean uploadFile(String filePath, FileChannel channel) throws IOException {
        if(!filePath.contains("/")){
            log.error("错误的文件路径，没有以/开头："+filePath);
            return false;
        }
        if(!filePath.startsWith("/")){
            log.error("文件路径出错，没有以/开头："+filePath);
            return false;
        }
        if(filePath.endsWith("/")){
            log.error("不是一个文件路径，请不要用/结尾："+filePath);
            return false;
        }
        long fileSize = channel.size();
        long curPosition = channel.position();
        String name = filePath.substring(filePath.lastIndexOf('/')+1);
        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setFileLength(fileSize);
        clientFileInfo.setPath(filePath);
        clientFileInfo.setName(name);
        ClientFileInfo uploadFileRes = clientProtocol.uploadFile(clientFileInfo);
        if (uploadFileRes==null||uploadFileRes.getRes()==null){
            log.error("上传失败: King回复为null");
            return false;
        }
        switch (uploadFileRes.getRes()){
            case SUCCESS:
                int pin = 0;
                while (true) {
                    try {
                        channel.position(curPosition);
                        uploadFile(uploadFileRes.getINodeFile(), channel);
                        clientProtocol.uploadFileOk(uploadFileRes);
                        return true;
                    } catch (Exception e) {
                        log.error("上传失败！！！！  "+e.getMessage());
                        pin++;
                        if(pin>=5) break;
                    }
                }
                break;
            case UNKNOWN:
                log.error("未知错误");
                break;
            case NO_SUCH_DIR:
                log.error("文件夹不存在");
                break;
            case NO_SUCH_File:
                log.error("文件不存在");
                break;
            case ALLREADY_EXITS:
                log.error("文件已存在");
                break;
            case IN_SAFE_MODE:
                log.error("保护模式");
                break;
        }
        return false;
    }

    @Override
    public ByteBuffer downloadFile(String filePath) {
        if(!filePath.contains("/")){
            log.error("错误的文件路径，没有以/开头："+filePath);
            return null;
        }
        if(!filePath.startsWith("/")){
            log.error("文件路径出错，没有以/开头："+filePath);
            return null;
        }
        if(filePath.endsWith("/")){
            log.error("不是一个文件路径，请不要用/结尾："+filePath);
            return null;
        }
        ClientFileInfo clientFileInfo = new ClientFileInfo();
        String name = filePath.substring(filePath.lastIndexOf('/')+1);
        clientFileInfo.setPath(filePath);
        clientFileInfo.setName(name);
        ClientFileInfo downloadFileRes = clientProtocol.downloadFile(clientFileInfo);
        if(downloadFileRes==null||downloadFileRes.getRes()==null){
            log.error("下载失败: King回复为null");
            return null;
        }
        switch (downloadFileRes.getRes()){
            case SUCCESS:
                int pin = 0;
                while (true) {
                    try {
                        return downloadFile(downloadFileRes.getINodeFile());
                    } catch (Exception e) {
                        log.error("下载失败！！！！  "+e.getMessage());
                        pin++;
                        if(pin>=5) break;
                    }
                }
                break;
            case UNKNOWN:
                log.error("未知错误");
                break;
            case NO_SUCH_DIR:
                log.error("文件夹不存在");
                break;
            case NO_SUCH_File:
                log.error("文件不存在");
                break;
            case ALLREADY_EXITS:
                log.error("文件已存在");
                break;
            case IN_SAFE_MODE:
                log.error("保护模式");
                break;
        }
        return null;
    }

    @Override
    public boolean mkdir(String dirPath) {
        if (dirPath.endsWith("/")) dirPath = dirPath.substring(0,dirPath.lastIndexOf('/'));
        if(!dirPath.contains("/")){
            log.error("错误的文件路径，没有以/开头："+dirPath);
            return false;
        }
        if(!dirPath.startsWith("/")){
            log.error("文件路径出错，没有以/开头："+dirPath);
            return false;
        }
        String name = dirPath.substring(dirPath.lastIndexOf('/')+1);
        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath(dirPath+"/");
        clientFileInfo.setName(name+"/");
        ClientFileInfo mkdir = clientProtocol.mkdir(clientFileInfo);
        if(mkdir==null||mkdir.getRes()==null){
            log.error("创建失败: King回复为null");
            return false;
        }
        switch (mkdir.getRes()){
            case SUCCESS:
                log.info("创建目录成功");
                return true;
            case UNKNOWN:
                log.error("未知错误");
                break;
            case NO_SUCH_DIR:
                log.error("文件夹不存在");
                break;
            case NO_SUCH_File:
                log.error("文件不存在");
                break;
            case ALLREADY_EXITS:
                log.error("文件已存在");
                break;
            case IN_SAFE_MODE:
                log.error("保护模式");
                break;
        }
        return false;
    }

    @Override
    public boolean rmFile(String filePath) {
        if(!filePath.contains("/")){
            log.error("错误的文件路径，没有以/开头："+filePath);
            return false;
        }
        if(!filePath.startsWith("/")){
            log.error("文件路径出错，没有以/开头："+filePath);
            return false;
        }
        if(filePath.endsWith("/")){
            log.error("不是一个文件路径，请不要用/结尾："+filePath);
            return false;
        }
        String name = filePath.substring(filePath.lastIndexOf('/')+1);
        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath(filePath);
        clientFileInfo.setName(name);
        ClientFileInfo rmFileRes = clientProtocol.rmFile(clientFileInfo);
        if(rmFileRes==null||rmFileRes.getRes()==null){
            log.error("删除失败: King回复为null");
            return false;
        }
        switch (rmFileRes.getRes()){
            case SUCCESS:
                log.info("删除文件成功");
                return true;
            case UNKNOWN:
                log.error("未知错误");
                break;
            case NO_SUCH_DIR:
                log.error("文件夹不存在");
                break;
            case NO_SUCH_File:
                log.error("文件不存在");
                break;
            case ALLREADY_EXITS:
                log.error("文件已存在");
                break;
            case IN_SAFE_MODE:
                log.error("保护模式");
                break;
        }
        return false;
    }

    @Override
    public boolean rmDir(String dirPath) {
        if (dirPath.endsWith("/")) dirPath = dirPath.substring(0,dirPath.lastIndexOf('/'));
        if(!dirPath.contains("/")){
            log.error("错误的文件路径，没有以/开头："+dirPath);
            return false;
        }
        if(!dirPath.startsWith("/")){
            log.error("文件路径出错，没有以/开头："+dirPath);
            return false;
        }
        String name = dirPath.substring(dirPath.lastIndexOf('/')+1);
        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath(dirPath+"/");
        clientFileInfo.setName(name+"/");
        ClientFileInfo rmDirRes = clientProtocol.rmDir(clientFileInfo);
        if(rmDirRes==null||rmDirRes.getRes()==null){
            log.error("删除失败: King回复为null");
            return false;
        }
        switch (rmDirRes.getRes()){
            case SUCCESS:
                log.info("删除目录成功");
                return true;
            case UNKNOWN:
                log.error("未知错误");
                break;
            case NO_SUCH_DIR:
                log.error("文件夹不存在");
                break;
            case NO_SUCH_File:
                log.error("文件不存在");
                break;
            case ALLREADY_EXITS:
                log.error("文件已存在");
                break;
            case IN_SAFE_MODE:
                log.error("保护模式");
                break;
        }
        return false;
    }

    private ByteBuffer downloadFile(INodeFile iNodeFile) throws Exception {
        List<Block> blocks = iNodeFile.getBlocks();
        List<Block> orderBlocks = blocks.stream().sorted(Comparator.comparingInt(Block::getPin)).collect(Collectors.toList());
        int length = 0;
        for (Block block : orderBlocks) {
            length += block.getBlockLength();
        }
        ByteBuffer res = ByteBuffer.allocate(length);
        for (Block block : orderBlocks) {
            top.shauna.dfs.soldiermanager.bean.Block toSendBlock = new top.shauna.dfs.soldiermanager.bean.Block();
            toSendBlock.setFilePath(block.getFilePath());
            toSendBlock.setVersion(block.getTimeStamp());
            toSendBlock.setPin(block.getPin());
            toSendBlock.setUuid("");
            boolean flag = false;
            for (ReplicasInfo replicasInfo : block.getReplicasInfos()) {
                try {
                    SoldierServerProtocol referenceProxy = CommonUtil.getSoldierServerProtocol(replicasInfo);
                    top.shauna.dfs.soldiermanager.bean.Block resBlock = referenceProxy.getBlock(toSendBlock);
                    if (
                            resBlock.getContent() != null
                                    && resBlock.getContent().length == block.getBlockLength()
                            ) {
                        res.put(resBlock.getContent());
                        flag = true;
                        log.info("下载Block-" + block.getPin() + "成功");
                        break;
                    }
                }catch (Exception e){
                    log.info("下载Block-" + block.getPin() + "失败，重试中。。。");
                }
            }
            if (!flag){
                throw new Exception("Block-"+block.getPin()+"下载失败！！！");
            }
        }
        res.position(0);
        return res;
    }

    private void uploadFile(INodeFile iNodeFile, FileChannel channel) throws Exception {
        List<Block> blocks = iNodeFile.getBlocks();
        for (Block block : blocks) {
            top.shauna.dfs.soldiermanager.bean.Block toSendBlock = new top.shauna.dfs.soldiermanager.bean.Block();
            toSendBlock.setFilePath(block.getFilePath());
            toSendBlock.setPin(block.getPin());
            toSendBlock.setUuid("");
            toSendBlock.setVersion(System.currentTimeMillis());

            Integer length = block.getBlockLength();
            ByteBuffer buffer = ByteBuffer.allocate(length);
            channel.read(buffer);
            byte[] toSendData = buffer.array();

            toSendBlock.setContent(toSendData);
            toSendBlock.setMd5(CommonUtil.getMD5(toSendData));

            List<ReplicasInfo> replicasInfos = block.getReplicasInfos();
            for (ReplicasInfo replicasInfo:replicasInfos){
                if (replicasInfo.getMaster()!=null&&replicasInfo.getMaster()){
                    SoldierServerProtocol referenceProxy = CommonUtil.getSoldierServerProtocol(replicasInfo);
                    replicasInfos.remove(replicasInfo);
                    block.setReplicas(block.getReplicas()-1);
                    toSendBlock.setReplicasInfos(replicasInfos);
                    SoldierResponse soldierResponse = referenceProxy.uploadFile(toSendBlock);
                    switch (soldierResponse.getRes()){
                        case UNKNOWN:
                            throw new Exception("未知错误");
                        case SUCCESS:
                            log.info("上传soldier成功");
                    }
                    break;
                }
            }
        }
    }
}


