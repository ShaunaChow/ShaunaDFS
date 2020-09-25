package top.shauna.dfs.config;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 16:23
 * @E-Mail z1023778132@icloud.com
 */
public class PubConfig {

    private PubConfig(){}

    private static volatile PubConfig pubConfig = null;

    public static PubConfig getInstance(){
        if(pubConfig==null){
            synchronized (PubConfig.class){
                if(pubConfig==null){
                    pubConfig = new PubConfig();
                }
            }
        }
        return pubConfig;
    }

    private String rootDir;
    private String port;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }
}
