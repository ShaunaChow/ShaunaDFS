package top.shauna.dfs.kingmanager.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import top.shauna.dfs.ha.KingHAStatus;
import top.shauna.dfs.kingmanager.BlocksManager;
import top.shauna.dfs.kingmanager.LogManager;
import top.shauna.dfs.kingmanager.ShaunaFSManager;
import top.shauna.dfs.kingmanager.bean.ClientFileInfo;
import top.shauna.dfs.kingmanager.bean.LogItem;
import top.shauna.dfs.safemode.SafeModeLock;
import top.shauna.dfs.type.ClientProtocolType;

import java.lang.reflect.Method;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/14 20:12
 * @E-Mail z1023778132@icloud.com
 */
public class ShaunaFSManagerProxy implements MethodInterceptor {
    private static volatile ShaunaFSManagerProxy proxy;
    private ShaunaFSManager shaunaFSManager;
    private ShaunaFSManager shaunaFSManagerProxy;
    private LogManager logManager;

    private ShaunaFSManagerProxy(LogManager logManager){
        this.shaunaFSManager = new ShaunaFSManager();
        this.logManager = logManager;
    }

    public static ShaunaFSManagerProxy getInstance(LogManager logManager){
        if(proxy==null){
            synchronized (ShaunaFSManagerProxy.class){
                if (proxy==null){
                    proxy = new ShaunaFSManagerProxy(logManager);
                }
            }
        }
        return proxy;
    }

    public ShaunaFSManager getShaunaFSManager(){
        return shaunaFSManager;
    }

    public ShaunaFSManager getProxy(){
        if (shaunaFSManagerProxy==null) {
            synchronized (ShaunaFSManagerProxy.class){
                if (shaunaFSManagerProxy==null) {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(ShaunaFSManager.class);
                    enhancer.setCallback(this);
                    shaunaFSManagerProxy = (ShaunaFSManager) enhancer.create();
                }
            }
        }
        return shaunaFSManagerProxy;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object invoke;
        LogItem logItem = null;
        if (method.getName().equalsIgnoreCase("uploadFile")){
            ClientFileInfo clientFileInfo = (ClientFileInfo) args[0];
            if (SafeModeLock.inSafeMode()){
                clientFileInfo.setRes(ClientProtocolType.IN_SAFE_MODE);
                return null;
            }
            invoke = method.invoke(shaunaFSManager, args);
        }else if (method.getName().equalsIgnoreCase("uploadFileOk")){
            ClientFileInfo clientFileInfo = (ClientFileInfo) args[0];
            if (SafeModeLock.inSafeMode()){
                clientFileInfo.setRes(ClientProtocolType.IN_SAFE_MODE);
                return null;
            }
            invoke = method.invoke(shaunaFSManager, args);
            if (clientFileInfo.getRes()==ClientProtocolType.SUCCESS) {
                logItem = new LogItem("uploadFile", clientFileInfo, 1);
            }
        }else if (method.getName().equalsIgnoreCase("mkdir")){
            ClientFileInfo clientFileInfo = (ClientFileInfo) args[0];
            if (SafeModeLock.inSafeMode()){
                clientFileInfo.setRes(ClientProtocolType.IN_SAFE_MODE);
                return null;
            }
            invoke = method.invoke(shaunaFSManager, args);
            if (clientFileInfo.getRes()==ClientProtocolType.SUCCESS) {
                logItem = new LogItem("mkdir", clientFileInfo, 1);
            }
        }else if (method.getName().equalsIgnoreCase("rmr")){
            ClientFileInfo clientFileInfo = (ClientFileInfo) args[0];
            if (SafeModeLock.inSafeMode()){
                clientFileInfo.setRes(ClientProtocolType.IN_SAFE_MODE);
                return null;
            }
            invoke = method.invoke(shaunaFSManager, args);
            if (clientFileInfo.getRes()==ClientProtocolType.SUCCESS) {
                logItem = new LogItem("rmr", clientFileInfo, 1);
            }
        }else{
            invoke = method.invoke(shaunaFSManager, args);
        }
        logManager.saveLogItem(logItem);
        return invoke;
    }
}
