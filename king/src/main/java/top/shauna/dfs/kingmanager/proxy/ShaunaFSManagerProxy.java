package top.shauna.dfs.kingmanager.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import top.shauna.dfs.kingmanager.LogManager;
import top.shauna.dfs.kingmanager.ShaunaFSManager;
import top.shauna.dfs.kingmanager.bean.ClientFileInfo;
import top.shauna.dfs.kingmanager.bean.LogItem;

import java.lang.reflect.Method;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/14 20:12
 * @E-Mail z1023778132@icloud.com
 */
public class ShaunaFSManagerProxy implements MethodInterceptor {
    private static volatile ShaunaFSManagerProxy proxy;

    private ShaunaFSManagerProxy(){
        this.shaunaFSManager = new ShaunaFSManager();
        this.logManager = new LogManager();
    }

    public static ShaunaFSManagerProxy getInstance(){
        if(proxy==null){
            synchronized (ShaunaFSManagerProxy.class){
                if (proxy==null){
                    proxy = new ShaunaFSManagerProxy();
                }
            }
        }
        return proxy;
    }

    private ShaunaFSManager shaunaFSManager;
    private ShaunaFSManager shaunaFSManagerProxy;
    private LogManager logManager;

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
        LogItem logItem;
        if (method.getName().equalsIgnoreCase("uploadFile")){
            ClientFileInfo clientFileInfo = (ClientFileInfo) args[0];
            invoke = method.invoke(shaunaFSManager, args);
            logItem = new LogItem("uploadFile", clientFileInfo, -1);
        }else if (method.getName().equalsIgnoreCase("uploadFileOk")){
            ClientFileInfo clientFileInfo = (ClientFileInfo) args[0];
            invoke = method.invoke(shaunaFSManager, args);
            logItem = new LogItem("uploadFile", clientFileInfo, 1);
        }else if (method.getName().equalsIgnoreCase("mkdir")){
            ClientFileInfo clientFileInfo = (ClientFileInfo) args[0];
            invoke = method.invoke(shaunaFSManager, args);
            logItem = new LogItem("mkdir", clientFileInfo, 1);
        }else if (method.getName().equalsIgnoreCase("rmr")){
            ClientFileInfo clientFileInfo = (ClientFileInfo) args[0];
            invoke = method.invoke(shaunaFSManager, args);
            logItem = new LogItem("rmr", clientFileInfo, 1);
        }else{
            invoke = method.invoke(shaunaFSManager, args);
            logItem = null;
        }
        logManager.saveLogItem(logItem);
        return invoke;
    }
}
