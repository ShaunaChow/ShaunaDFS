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
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.List;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 21:55
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ClientServiceImpl implements ClientService {
    private ClientProtocol clientProtocol;

    public ClientServiceImpl(){
        preparePubConfig();
        clientProtocol = ShaunaRPCHandler.getReferenceProxy(ClientProtocol.class);
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
        }
        return false;
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
            toSendBlock.setMd5(getMD5(toSendData));

            List<ReplicasInfo> replicasInfos = block.getReplicasInfos();
            for (ReplicasInfo replicasInfo:replicasInfos){
                if (replicasInfo.getMaster()!=null&&replicasInfo.getMaster()){
                    LocalExportBean localExportBean = new LocalExportBean("netty", Integer.parseInt(replicasInfo.getPort()), replicasInfo.getIp());
                    SoldierServerProtocol referenceProxy = ShaunaRPCHandler.getReferenceProxy(SoldierServerProtocol.class, localExportBean);
                    SoldierResponse soldierResponse = referenceProxy.uploadFile(toSendBlock);
                    switch (soldierResponse.getRes()){
                        case UNKNOWN:
                            throw new Exception("未知错误");
                        case SUCCESS:
                            log.info("上传soldier成功");
                    }
                }
            }
        }
    }

    private String getMD5(byte[] bb){
        MessageDigest md = null;
        String res = null;
        try {
            String str = new String(bb);
            md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            res = new BigInteger(1, md.digest()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


}


