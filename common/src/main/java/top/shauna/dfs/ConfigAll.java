package top.shauna.dfs;

import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @Author Shauna.Chow
 * @Date 2021/11/27 17:01
 * @E-Mail z1023778132@icloud.com
 */
public class ConfigAll {
    private static String rootPath = null;
    private static Scanner scanner = null;
    public static void main(String[] args) throws IOException {
        System.out.print("欢迎来到ShaunaDfs配置系统，以下配置中*为必填，其他选项的括号中内容为默认值，按下Enter以开始配置！");
        scanner = new Scanner(System.in);
        scanner.nextLine();
        configFileRootPath();
        System.out.println("接下来开始配置king");
        configKings();
        System.out.println("接下来开始配置queen");
        configQueen();
        System.out.println("soldiers");
        configSoldiers();

    }

    private static void configSoldiers() {
        System.out.println("请输入soldier数量：");
        int soldierNum = scanner.nextInt();
        scanner.nextLine();
        List<Map<String,String>> configs = new ArrayList<>(soldierNum);
        for (int i = 0; i < soldierNum; i++) {
            System.out.println("当前 配置第"+(i+1)+"个soldier：");
            System.out.println("请配置ip地址：");
            String ip = scanner.nextLine();
            System.out.println("请配置监听端口：");
            int port = scanner.nextInt();
            scanner.nextLine();
            Map<String,String> map = new HashMap<>();
            map.put("rootPath",rootPath+File.separator+"soldier"+(i+1));
            map.put("port",String.valueOf(port));
            map.put("threadNums","20");
            map.put("exportIP",ip);
            map.put("shaunaRpc.applicationName","shaunaDfs");
            map.put("shaunaRpc.threadNum","10");
            map.put("shaunaRpc.timeout","500000");
            map.put("shaunaRpc.registerBean.potocol","zookeeper");
            map.put("shaunaRpc.foundBean.potocol","zookeeper");
            map.put("shaunaRpc.registerBean.loc","");
            map.put("shaunaRpc.foundBean.loc","");
            System.out.println("请输入zookeeper地址（ip:port）：");
            String zk = scanner.nextLine();
            map.put("shaunaRpc.registerBean.url",zk);
            map.put("shaunaRpc.foundBean.url",zk);
            configs.add(map);
        }
        for (int i=0;i<soldierNum;i++) {
            Map<String, String> config = configs.get(i);
            PrintStream ps = null;
            try {
                ps = new PrintStream(new FileOutputStream(rootPath+File.separator+"soldier"+(i+1)+".properties"));
                for (Map.Entry<String, String> entry : config.entrySet()) {
                    ps.println(entry.getKey()+"="+entry.getValue());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                ps.close();
            }
        }
        System.out.println("配置已完成，打开目录"+rootPath+"以查看配置");
    }

    private static void configQueen() {
        PrintStream ps = null;
        try {
            System.out.println("请输入queen监听的端口：");
            int port = scanner.nextInt();
            scanner.nextLine();
            ps = new PrintStream(new FileOutputStream(rootPath+File.separator+"queen.properties"));
            ps.println("rootPath="+rootPath+File.separator+"queen");
            ps.println("port="+port);
            ps.println("threadNums=10");
            ps.println("editLogDirs="+rootPath+File.separator+"queen");
            ps.println("shaunaRpc.applicationName=shaunaDfs");
            ps.println("shaunaRpc.threadNum=10");
            ps.println("shaunaRpc.timeout=5000");
            ps.println("shaunaRpc.registerBean.potocol=zookeeper");
            ps.println("shaunaRpc.registerBean.loc=");
            ps.println("shaunaRpc.foundBean.potocol=zookeeper");
            ps.println("shaunaRpc.foundBean.loc=");
            System.out.println("请输入zookeeper地址（ip:port）：");
            String zk = scanner.nextLine();
            ps.println("shaunaRpc.registerBean.url="+zk);
            ps.println("shaunaRpc.foundBean.url="+zk);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            ps.close();
        }
    }

    private static void configKings() {
        System.out.println("是否启用king的高可用？（y/n）");
        String ha = scanner.nextLine();
        int haNum = 1;
        if ("y".equalsIgnoreCase(ha)){
            System.out.println("请输入高可用king数量：");
            haNum = scanner.nextInt();
            scanner.nextLine();
        }
        List<Map<String,String>> configs = new ArrayList<>(haNum);
        StringBuilder haIps = new StringBuilder();
        for (int i = 0; i < haNum; i++) {
            System.out.println("当前 配置第"+(i+1)+"个king：");
            System.out.println("请配置ip地址：");
            String ip = scanner.nextLine();
            System.out.println("请配置一个起始端口：");
            int port = scanner.nextInt();
            scanner.nextLine();
            System.out.println("端口: "+port+"、"+(port+1)+"、"+(port+2)+"、"+(port+3)+"、"+(port+4)+"将被占用!");
            Map<String,String> map = new HashMap<>();
            map.put("rootPath",rootPath+File.separator+"king"+(i+1));
            map.put("editLogDirs",rootPath+File.separator+"king"+(i+1));
            map.put("soldierServerPort",String.valueOf(port));
            map.put("clientServerPort",String.valueOf(port+1));
            map.put("queenServerPort",String.valueOf(port+2));
            map.put("haPort",String.valueOf(port+3));
            map.put("monitorPort",String.valueOf(port+4));
            haIps.append(ip+":"+(port+4));
            if (i!=haNum-1) haIps.append(",");
            map.put("exportIP",ip);
            map.put("threadNums","20");
            map.put("shaunaRpc.applicationName","shaunaDfs");
            map.put("shaunaRpc.threadNum","10");
            map.put("shaunaRpc.timeout","5000");
            map.put("shaunaRpc.registerBean.potocol","zookeeper");
            map.put("shaunaRpc.foundBean.potocol","zookeeper");
            map.put("shaunaRpc.registerBean.loc","");
            map.put("shaunaRpc.foundBean.loc","");
            System.out.println("请输入zookeeper地址（ip:port）：");
            String zk = scanner.nextLine();
            map.put("shaunaRpc.registerBean.url",zk);
            map.put("shaunaRpc.foundBean.url",zk);
            configs.add(map);
        }
        for (int i=0;i<haNum;i++) {
            Map<String, String> config = configs.get(i);
            PrintStream ps = null;
            try {
                ps = new PrintStream(new FileOutputStream(rootPath+File.separator+"king"+(i+1)+".properties"));
                for (Map.Entry<String, String> entry : config.entrySet()) {
                    ps.println(entry.getKey()+"="+entry.getValue());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                ps.close();
            }
        }
    }

    private static void configFileRootPath() throws IOException {
        System.out.println("* 请输入所有数据的根目录：");
        String tmp = scanner.nextLine();
        if (tmp.endsWith("/")) tmp = tmp.substring(0,tmp.length()-1);
        while (StringUtils.isEmpty(tmp)) {
            System.out.println("* 请输入所有数据的根目录：");
            tmp = scanner.nextLine();
            if (tmp.endsWith("/")) tmp = tmp.substring(0,tmp.length()-1);
        }
        File file = new File(tmp);
        if (!file.exists()){
            file.mkdir();
        }else{
            if (file.isFile()) {
                file.delete();
                file.mkdir();
            }
        }
        rootPath = file.getAbsolutePath();
    }
}
