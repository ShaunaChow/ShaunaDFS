package top.shauna.dfs.monitor;

import top.shauna.dfs.soldiermanager.bean.Block;
import top.shauna.dfs.block.LocalBlockHandler;
import top.shauna.dfs.block.interfaces.BlockHandler;
import top.shauna.dfs.monitor.bean.StaticBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 10:50
 * @E-Mail z1023778132@icloud.com
 */
public class MonitorProxy implements InvocationHandler {
    private BlockHandler abstractBlockHandler;

    public MonitorProxy(){
        abstractBlockHandler = new LocalBlockHandler();
    }

    public MonitorProxy(BlockHandler blockHandler){
        this.abstractBlockHandler = blockHandler;
    }

    public BlockHandler getProxy(){
        return (BlockHandler)Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{BlockHandler.class},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        long t1 = System.currentTimeMillis();
        Object invoke = method.invoke(abstractBlockHandler, args);
        long t2 = System.currentTimeMillis();

        StaticBean staticBean = new StaticBean(t1,t2, (Block) args[0]);
        if(method.getName().contains("write")){
            StaticDatas.addWriteData(staticBean);
        }else if(method.getName().contains("read")){
            StaticDatas.addReadData(staticBean);
        }

        return invoke;
    }
}
