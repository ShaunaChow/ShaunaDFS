package top.shauna.dfs.safemode;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/22 19:27
 * @E-Mail z1023778132@icloud.com
 */
public class SafeModeLock {
    private static volatile boolean blockOk;
    private static volatile boolean queenOk;

    public static synchronized boolean inSafeMode(){       /** 返回true为保护模式，false为非保护模式 **/
        return !(blockOk && queenOk);
    }

    public static synchronized void setQueenOk(boolean flag){
        queenOk = flag;
    }

    public static synchronized void setBlockOk(boolean flag){
        blockOk = flag;
    }
}
