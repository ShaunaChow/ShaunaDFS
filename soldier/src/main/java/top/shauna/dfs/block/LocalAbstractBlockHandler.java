package top.shauna.dfs.block;

import com.alibaba.fastjson.JSON;
import top.shauna.dfs.bean.Block;
import top.shauna.dfs.bean.DataInfo;
import top.shauna.dfs.bean.MetaInfo;
import top.shauna.dfs.block.interfaces.AbstractBlockHandler;
import top.shauna.dfs.storage.impl.LocalFileStorage;

import java.io.File;
import java.nio.channels.WritableByteChannel;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 17:01
 * @E-Mail z1023778132@icloud.com
 */
public class LocalAbstractBlockHandler extends AbstractBlockHandler {
    private LocalFileStorage localFileStorage;

    public LocalAbstractBlockHandler(){
        localFileStorage = LocalFileStorage.getInstance();
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
            throw new Exception("文件" + dataPath + "已经存在！！！");
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
        byte[] content = JSON.toJSONString(metaInfo).getBytes();
        String dataPath = metaInfo.getMetaPath();
        if(localFileStorage.isExits(dataPath)) {
            throw new Exception("文件" + dataPath + "已经存在！！！");
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
