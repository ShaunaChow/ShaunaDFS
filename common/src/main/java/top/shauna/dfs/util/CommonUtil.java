package top.shauna.dfs.util;

import top.shauna.dfs.kingmanager.bean.ReplicasInfo;
import top.shauna.dfs.protocol.SoldierServerProtocol;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/26 14:17
 * @E-Mail z1023778132@icloud.com
 */
public class CommonUtil {
    private static ConcurrentHashMap<String,SoldierServerProtocol> connectKeeper = new ConcurrentHashMap<>();

    public static String getMD5(byte[] bb){
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
}
