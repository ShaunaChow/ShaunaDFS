package top.shauna.dfs.client.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import top.shauna.dfs.kingmanager.bean.ClientFileInfo;
import top.shauna.dfs.kingmanager.bean.INodeDirectory;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.dfs.service.ClientService;
import top.shauna.dfs.service.impl.ClientServiceImpl;
import top.shauna.dfs.util.CommonUtil;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.LocalExportBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;
import top.shauna.rpc.service.ShaunaRPCHandler;
import top.shauna.rpc.supports.ZKSupportKit;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 17:36
 * @E-Mail z1023778132@icloud.com
 */
public class Test1 {

    @Test
    public void test1() {
        preparePubConfig();

        ClientProtocol proxy = ShaunaRPCHandler.getReferenceProxy(ClientProtocol.class);

        mkdir("/shauna", proxy);
        mkdir("/shauna/test", proxy);

        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath("/shauna/test/tt1.txt");
        clientFileInfo.setName("tt1.txt");
        clientFileInfo.setFileLength(200L);

        ClientFileInfo uploadFile = proxy.uploadFile(clientFileInfo);
        System.out.println(uploadFile);

        proxy.uploadFileOk(clientFileInfo);

        ClientFileInfo downloadFile = proxy.downloadFile(clientFileInfo);
        System.out.println(downloadFile);

        clientFileInfo.setPath("/shauna");
        clientFileInfo.setName("shauna/");

        ClientFileInfo rmFile = proxy.rmDir(clientFileInfo);
        System.out.println(rmFile);
    }

    @Test
    public void test2() throws IOException {
        preparePubConfig();

        ClientProtocol proxy = ShaunaRPCHandler.getReferenceProxy(ClientProtocol.class);

        mkdir("/shauna", proxy);
        mkdir("/mago", proxy);
        mkdir("/yizhi", proxy);
        mkdir("/shauna/test", proxy);

        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath("/shauna/test/tt1.txt");
        clientFileInfo.setName("tt1.txt");
        clientFileInfo.setFileLength(200L);

        ClientFileInfo uploadFile = proxy.uploadFile(clientFileInfo);
        System.out.println(uploadFile);

        proxy.uploadFileOk(clientFileInfo);

        System.in.read();
    }

    private void mkdir(String path, ClientProtocol proxy) {
        ClientFileInfo clientFileInfo = new ClientFileInfo();
        clientFileInfo.setPath(path);
        clientFileInfo.setName(path.substring(path.lastIndexOf("/") + 1));
        ClientFileInfo mkdir = proxy.mkdir(clientFileInfo);
        System.out.println(mkdir);
    }

    @Test
    public void test3() throws Exception {
        preparePubConfig();
        ClientService clientService = new ClientServiceImpl();//new LocalExportBean("netty",10001,"39.105.89.185"));
        INodeDirectory dir = clientService.getDir("/");
        System.out.println(dir);
//        clientService.mkdir("/1/");
//        clientService.mkdir("/2/");
//        clientService.mkdir("/3/");
//        clientService.mkdir("/4/");
//        clientService.mkdir("/5/");
//        clientService.mkdir("/6/");
    }

    @Test
    public void test433() throws Exception {
        preparePubConfig();

        FileChannel fileChannel = new RandomAccessFile("H:\\百度网盘文件\\Netty\\3-介绍3.png",
                "rw").getChannel();

        ClientService clientService = new ClientServiceImpl();

//        clientService.mkdir("/shauna/");

        ByteBuffer allocate = ByteBuffer.allocate((int) fileChannel.size());

        fileChannel.read(allocate);

        clientService.uploadFile("/1.png", allocate.array());

        ByteBuffer byteBuffer = clientService.downloadFile("/shauna/2.rar");

        FileChannel fileChannel2 = new RandomAccessFile("H:\\百度网盘文件\\3-介绍3.png",
                "rw").getChannel();

        fileChannel2.write(byteBuffer);

        fileChannel.close();
        fileChannel2.close();

        System.out.println(JSON.toJSONString(clientService.getDir("/")));
    }

    private void preparePubConfig() {
        PubConfig pubConfig = PubConfig.getInstance();
        pubConfig.setTimeout(100000L);
        if (pubConfig.getRegisterBean() == null) {
            RegisterBean registerBean = new RegisterBean("zookeeper", "39.105.89.185:2181", null);
            pubConfig.setRegisterBean(registerBean);
        }
        if (pubConfig.getFoundBean() == null) {
            RegisterBean registerBean = pubConfig.getRegisterBean();
            FoundBean foundBean = new FoundBean(
                    registerBean.getPotocol(),
                    registerBean.getUrl(),
                    registerBean.getLoc()
            );
            pubConfig.setFoundBean(foundBean);
        }
    }

    @Test
    public void test4() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("F:\\java项目\\ShaunaDfsTmp2"+File.separator+"edit_"+".log");
        fileOutputStream.write(new byte[]{96,95,97,98,100});
        fileOutputStream.flush();

        System.in.read();
    }

    @Test
    public void test5() throws IOException {
        File file = new File("F:\\java项目\\ShaunaDfsTmp");

        System.out.println(file.getTotalSpace()/1024.0/1024.0/1024.0);

        System.out.println(file.getFreeSpace()/1024.0/1024.0/1024.0);

        Map<String,Integer> map = new ConcurrentHashMap<>();
        map.put("1",1);
        map.put("2",2);
        map.put("3",3);
        for (String key:map.keySet()){
            map.remove(key);
        }
        System.out.println(map.get("1"));
    }

    @Test
    public void ok(){
        ZKSupportKit zkSupportKit = new ZKSupportKit("39.105.89.185:2181",5000);
        List<String> strings = zkSupportKit.readChildren("/shauna/top.shauna.dfs.protocol.ClientProtocol/providers");
        for (String string : strings) {
            System.out.println(string);
        }
        strings = zkSupportKit.readChildren("/shauna/top.shauna.dfs.protocol.HeartBeatProtocol/providers");
        for (String string : strings) {
            System.out.println(string);
        }
        strings = zkSupportKit.readChildren("/shauna/top.shauna.dfs.protocol.SoldierServerProtocol/providers");
        for (String string : strings) {
            System.out.println(string);
        }
        System.out.println(CommonUtil.getLocalHostIp());
        Map<Integer,Integer> map = new HashMap<>();
        System.out.println(map.containsKey(null));

        CopyOnWriteArrayList<Integer> integers = new CopyOnWriteArrayList<>();
        integers.add(1);
        integers.add(2);
        for (int i=0;i<integers.size();i++){
            Integer integer = integers.get(i);
            if (integer==1){
                integers.remove(i--);
            }
        }
        for (Integer integer : integers) {
            System.out.println(integer);
        }
    }

    @Test
    public void okkkkkk(){
        byte[] bytes = {1, 2, 3, 4, 5, 6, 7, 8};
        byte[] by = Arrays.copyOfRange(bytes, 0, 2);
        for(byte b:by){
            System.out.println(b);
        }
        by = Arrays.copyOfRange(bytes, 2, 4);
        for(byte b:by){
            System.out.println(b);
        }
    }
}