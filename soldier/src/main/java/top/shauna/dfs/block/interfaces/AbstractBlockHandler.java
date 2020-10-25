package top.shauna.dfs.block.interfaces;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.soldiermanager.bean.Block;
import top.shauna.dfs.soldiermanager.bean.DataInfo;
import top.shauna.dfs.soldiermanager.bean.MetaInfo;
import top.shauna.dfs.block.DataKeeper;
import top.shauna.dfs.block.MetaKeeper;
import top.shauna.dfs.config.SoldierPubConfig;

import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.security.MessageDigest;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 16:02
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public abstract class AbstractBlockHandler implements BlockHandler{

    @Override
    public final void write(Block block) throws Exception {
        String md5 = getMD5(block.getContent());
        if(!isValid(block,md5)) {
            throw new Exception("block不合法！！！");
        }

        SoldierPubConfig soldierPubConfig = SoldierPubConfig.getInstance();

        String metaDataDir = soldierPubConfig.getRootDir()+ File.separator+"Meta";
        String metaPath = metaDataDir + block.getFilePath()+"_"+block.getPin()+".block";
        MetaInfo metaInfo = null;
        if(MetaKeeper.contains(metaPath)){
            log.warn("Block已经存在，即将覆盖");
            MetaInfo oldMetaInfo = MetaKeeper.get(metaPath);
            DataInfo oldDataInfo = oldMetaInfo.getDataInfo();
            if (oldDataInfo.getMd5().equals(md5)){
                return;
            }else{
                DataKeeper.delete(oldDataInfo.getMd5());
                metaInfo = oldMetaInfo;
            }
        }

        if (metaInfo==null) metaInfo = new MetaInfo();
        metaInfo.setFilePath(block.getFilePath());
        metaInfo.setMetaPath(metaPath);
        metaInfo.setPin(block.getPin());
        metaInfo.setVersion(block.getVersion());

        String dataPath;
        DataInfo dataInfo;
        if(!DataKeeper.contains(md5)){
            String dataDir = soldierPubConfig.getRootDir()+ File.separator+"Data";
            dataPath = dataDir + block.getFilePath()+"_"+block.getPin()+".block";
            writeToData(dataPath,block);
            dataInfo = new DataInfo();
            dataInfo.setDataPath(dataPath);
            dataInfo.setMd5(md5);
            dataInfo.setReference(1);
            if(isMemEnough()){
                dataInfo.setContent(block.getContent());
            }
            DataKeeper.put(md5,dataInfo);
        }else{
            dataInfo = DataKeeper.get(md5);
            dataInfo.setReference((dataInfo.getReference()==null?0:dataInfo.getReference())+1);
        }
        metaInfo.setDataInfo(dataInfo);

        writeToMetaData(metaInfo,block);
        MetaKeeper.put(metaPath,metaInfo);
    }

    @Override
    public final void read(Block block) throws Exception {
        SoldierPubConfig soldierPubConfig = SoldierPubConfig.getInstance();
        String metaDataDir = soldierPubConfig.getRootDir()+ File.separator+"Meta";
        String metaPath = metaDataDir + block.getFilePath()+"_"+block.getPin()+".block";
        if(MetaKeeper.contains(metaPath)){
            MetaInfo metaInfo = MetaKeeper.get(metaPath);
            byte[] content = metaInfo.getDataInfo().getContent();
            if(content !=null){
                block.setContent(content);
                block.setMd5(metaInfo.getDataInfo().getMd5());
                return;
            }
            content = readData(metaInfo.getDataInfo());
            block.setContent(content);
            block.setMd5(metaInfo.getDataInfo().getMd5());
            return;
        }
        throw new Exception("本机未找到block对应的块文件:"+block.getFilePath()+"_"+block.getPin()+".block");
    }

    @Override
    public final void readAndTransfer(Block block, WritableByteChannel channel) throws Exception {
        SoldierPubConfig soldierPubConfig = SoldierPubConfig.getInstance();
        String metaDataDir = soldierPubConfig.getRootDir()+ File.separator+"Meta";
        String metaPath = metaDataDir + block.getFilePath()+"_"+block.getPin()+".block";
        if(MetaKeeper.contains(metaPath)){
            MetaInfo metaInfo = MetaKeeper.get(metaPath);
            byte[] content = metaInfo.getDataInfo().getContent();
            if(content != null){
                channel.write(ByteBuffer.wrap(content));
                return;
            }
            transferData(metaInfo.getDataInfo(),channel);
            return;
        }
        throw new Exception("本机未找到block对应的块文件:"+block.getFilePath()+"_"+block.getPin()+".block");
    }

    protected abstract void transferData(DataInfo dataInfo, WritableByteChannel channel) throws Exception;

    protected abstract byte[] readData(DataInfo dataInfo) throws Exception;

    private boolean isMemEnough(){
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        if( ((double) (freeMemory+maxMemory-totalMemory)/(double) maxMemory)<0.2 ){
            return false;
        }
        return true;
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

    protected abstract void writeToData(String dataPath, Block block) throws Exception;

    protected abstract void writeToMetaData(MetaInfo metaInfo, Block block) throws Exception;

    protected abstract boolean isValid(Block block, String md5);
}
