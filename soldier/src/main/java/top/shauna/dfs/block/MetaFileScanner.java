package top.shauna.dfs.block;

import com.alibaba.fastjson.JSON;
import top.shauna.dfs.bean.MetaInfo;
import top.shauna.dfs.config.PubConfig;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.storage.impl.LocalFileStorage;

import java.io.File;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 19:48
 * @E-Mail z1023778132@icloud.com
 */
public class MetaFileScanner implements Starter {
    @Override
    public void onStart() throws Exception {
        scanMetaFiles();
    }

    private void scanMetaFiles() throws Exception {
        String rootPath = PubConfig.getInstance().getRootDir()+File.separator+"Meta";
        File file = new File(rootPath);
        scanFile(file);
    }

    private void scanFile(File file) throws Exception {
        if(file.isDirectory()){
            for (File f : file.listFiles()) {
                scanFile(f);
            }
        }else if(file.getName().endsWith(".block")){
            byte[] readBytes = LocalFileStorage.getInstance().read(file);
            MetaInfo metaInfo = JSON.parseObject(new String(readBytes), MetaInfo.class);
            String metaKey = PubConfig.getInstance().getRootDir()+ File.separator+"Meta"+
                                File.separator+metaInfo.getFilePath()+"_"+metaInfo.getPin()+".block";
            MetaKeeper.put(metaKey,metaInfo);
            String dataKey = metaInfo.getDataInfo().getMd5();
            DataKeeper.put(dataKey,metaInfo.getDataInfo());
        }
    }
}
