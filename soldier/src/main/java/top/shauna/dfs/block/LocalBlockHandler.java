package top.shauna.dfs.block;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.soldiermanager.bean.Block;
import top.shauna.dfs.soldiermanager.bean.DataInfo;
import top.shauna.dfs.soldiermanager.bean.MetaInfo;
import top.shauna.dfs.block.interfaces.AbstractBlockHandler;
import top.shauna.dfs.storage.impl.LocalFileStorage;
import top.shauna.rpc.protocol.serializer.HessianSerializer;

import java.io.File;
import java.nio.channels.WritableByteChannel;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 17:01
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class LocalBlockHandler extends AbstractBlockHandler {
    private LocalFileStorage localFileStorage;
    private HessianSerializer hessianSerializer;

    public LocalBlockHandler(){
        localFileStorage = LocalFileStorage.getInstance();
        hessianSerializer = new HessianSerializer();
    }

    @Override
    protected void transferData(DataInfo dataInfo, WritableByteChannel channel) throws Exception {
        try {
            localFileStorage.read(dataInfo.getDataPath(),channel);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected byte[] readData(DataInfo dataInfo) throws Exception {
        try {
            byte[] read = localFileStorage.read(dataInfo.getDataPath());
            return read;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void writeToData(String dataPath, Block block) throws Exception {
        if(localFileStorage.isExits(dataPath)) {
            log.warn("文件" + dataPath + "已经存在！！！");
        }
        File parentFile = new File(dataPath).getParentFile();
        if(!localFileStorage.isExits(parentFile)){
            parentFile.mkdirs();
        }
        try {
            localFileStorage.write(dataPath,block.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void writeToMetaData(MetaInfo metaInfo, Block block) throws Exception {
        DataInfo dataInfo = metaInfo.getDataInfo();
        MetaInfo meta = new MetaInfo(
                metaInfo.getFilePath(),
                metaInfo.getPin(),
                metaInfo.getVersion(),
                metaInfo.getMetaPath(),
                new DataInfo(dataInfo.getDataPath(),dataInfo.getMd5(),null,dataInfo.getReference())
        );
        byte[] content = hessianSerializer.getData(meta);
        String dataPath = meta.getMetaPath();
        if(localFileStorage.isExits(dataPath)) {
            log.warn("文件" + dataPath + "已经存在！！！");
        }
        File parentFile = new File(dataPath).getParentFile();
        if(!localFileStorage.isExits(parentFile)){
            parentFile.mkdirs();
        }
        try {
            localFileStorage.write(dataPath,content);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected boolean isValid(Block block, String md5) {
        if(md5.equals(block.getMd5())) return true;
        return false;
    }
}
