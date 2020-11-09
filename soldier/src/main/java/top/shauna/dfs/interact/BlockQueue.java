package top.shauna.dfs.interact;

import top.shauna.dfs.kingmanager.bean.Transaction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chow
 * @Date 2020/11/9 20:37
 * @E-Mail z1023778132@icloud.com
 */
public class BlockQueue {
    private CopyOnWriteArrayList<Transaction> list;

    public BlockQueue(){
        list = new CopyOnWriteArrayList<>();
    }

    public synchronized void addAll(List<Transaction> toAdd){
        list.addAll(toAdd);
        notifyAll();
    }

    public synchronized Transaction take() throws InterruptedException {
        while (list.size()==0){
            wait();
        }
        return list.remove(list.size()-1);
    }
}
