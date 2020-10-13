package top.shauna.dfs.block;

import top.shauna.dfs.soldiermanager.bean.MetaInfo;
import top.shauna.dfs.config.SoldierPubConfig;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.storage.impl.LocalFileStorage;
import top.shauna.rpc.protocol.serializer.HessianSerializer;

import java.io.File;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 19:48
 * @E-Mail z1023778132@icloud.com
 */
public class MetaFileScanner implements Starter {
    private HessianSerializer hessianSerializer;

    @Override
    public void onStart() throws Exception {
        hessianSerializer = new HessianSerializer();
        scanMetaFiles();
    }

    private void scanMetaFiles() throws Exception {
        String rootPath = SoldierPubConfig.getInstance().getRootDir()+File.separator+"Meta";
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
            MetaInfo metaInfo = (MetaInfo) hessianSerializer.getObj(readBytes);
            String metaKey = SoldierPubConfig.getInstance().getRootDir()+ File.separator+"Meta"+
                                metaInfo.getFilePath()+"_"+metaInfo.getPin()+".block";
            MetaKeeper.put(metaKey,metaInfo);
            String dataKey = metaInfo.getDataInfo().getMd5();
            DataKeeper.put(dataKey,metaInfo.getDataInfo());
        }
    }
}
