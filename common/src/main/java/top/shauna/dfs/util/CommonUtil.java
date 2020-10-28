package top.shauna.dfs.util;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.kingmanager.bean.CheckPoint;
import top.shauna.dfs.kingmanager.bean.INodeDirectory;
import top.shauna.dfs.kingmanager.bean.LogItem;
import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.protocol.SoldierServerProtocol;
import top.shauna.dfs.storage.impl.LocalFileStorage;
import top.shauna.dfs.storage.interfaces.StorageEngine;
import top.shauna.dfs.storage.util.CheckPointUtil;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 14:17
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class CommonUtil {
    private static ConcurrentHashMap<String,SoldierServerProtocol> connectKeeper = new ConcurrentHashMap<>();
    private static StorageEngine storageEngine = LocalFileStorage.getInstance();

    public static String getMD5(byte[] bb){
        MessageDigest md;
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

    public static SoldierServerProtocol getSoldierServerProtocol(ReplicasInfo replicasInfo) throws Exception {
        String key = replicasInfo.getIp()+":"+replicasInfo.getPort();
        if (connectKeeper.containsKey(key)) return connectKeeper.get(key);
        LocalExportBean localExportBean = new LocalExportBean("netty", Integer.parseInt(replicasInfo.getPort()), replicasInfo.getIp());
        SoldierServerProtocol referenceProxy = ShaunaRPCHandler.getReferenceProxy(SoldierServerProtocol.class, localExportBean);
        connectKeeper.put(key,referenceProxy);
        return referenceProxy;
    }

    public static String getLocalHostIp(){
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }
    }

    public static byte[] dealWithCheckPoint(CheckPoint checkPoint) throws Exception {
        byte[] image = checkPoint.getShaunaImage();
        DataInputStream imageInput = new DataInputStream(new ByteArrayInputStream(image));
        byte[] editLog = checkPoint.getEditLog();
        DataInputStream logInput = new DataInputStream(new ByteArrayInputStream(editLog));
        INodeDirectory root = CheckPointUtil.loadRootNode(imageInput);
        List<LogItem> editLogs = CheckPointUtil.loadEditLogs(logInput);
        CheckPointUtil.mergeEditLogs(root,editLogs);
        byte[] newImage = CheckPointUtil.saveRootNode(root);
        return newImage;
    }

    public static void saveCheckPointLocal(byte[] image,String path) throws Exception {
        storageEngine.write(path,image);
    }

    public static CheckPoint getCheckPoint(File imageFile, File... editFiles) throws Exception {
        CheckPoint checkPoint = new CheckPoint();
        checkPoint.setShaunaImage(storageEngine.read(imageFile));
        ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
        for (File editFile : editFiles) {
            byte[] bytes = storageEngine.read(editFile);
            imageOutput.write(bytes);
            imageOutput.flush();
        }
        checkPoint.setEditLog(imageOutput.toByteArray());
        checkPoint.setStatus(1);
        return checkPoint;
    }

    public static List<File> scanEditLogFiles(String rootD) {
        File file = new File(rootD);
        if (!file.isDirectory()){
            log.error("根目录错误!!!");
            return null;
        }else{
            List<File> res = new ArrayList<>();
            for (File f : file.listFiles()) {
                if (f.isFile()&&f.getName().endsWith(".log")&&f.getName().startsWith("edit_")){
                    res.add(f);
                }
            }
            res.stream().sorted((f1,f2)->{
                String name1 = f1.getName();
                String name2 = f2.getName();
                int i1 = Integer.parseInt(name1.substring(name1.indexOf("_") + 1, name1.indexOf(".")));
                int i2 = Integer.parseInt(name2.substring(name2.indexOf("_") + 1, name2.indexOf(".")));
                return i1-i2;
            });
            return res;
        }
    }
}
