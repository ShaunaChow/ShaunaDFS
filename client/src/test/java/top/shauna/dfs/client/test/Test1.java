package top.shauna.dfs.client.test;

import org.junit.Test;
import top.shauna.dfs.kingmanager.bean.ClientFileInfo;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.dfs.service.ClientService;
import top.shauna.dfs.service.impl.ClientServiceImpl;
import top.shauna.rpc.bean.FoundBean;
import top.shauna.rpc.bean.RegisterBean;
import top.shauna.rpc.config.PubConfig;
import top.shauna.rpc.service.ShaunaRPCHandler;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
    public void test3() throws IOException {
        preparePubConfig();
        ClientService clientService = new ClientServiceImpl();

        clientService.mkdir("/sh2432/");
        clientService.mkdir("/sha2568u22/");
        clientService.mkdir("/sh3a89333/");
        clientService.mkdir("/shau2n44/");
        clientService.mkdir("/sha433u55/");
        clientService.mkdir("/sh22556/");
        clientService.mkdir("/sh634u57/");
        clientService.mkdir("/shn7655/");
        clientService.mkdir("/sh8a59/");
        clientService.mkdir("/sh56a55/");
        clientService.mkdir("/sa763455u59/");
        clientService.mkdir("/sha345555/");
    }

    @Test
    public void test433() throws IOException {
        preparePubConfig();

        FileChannel fileChannel = new RandomAccessFile("F:\\百度网盘下载\\jdk-8u201-windows-x64.exe",
                "rw").getChannel();
//      jdk-8u201-windows-x64.exe
//      2019优秀数模论文.zip
//      example.pdf
        ClientService clientService = new ClientServiceImpl();

        clientService.mkdir("/shauna/");

        clientService.uploadFile("/shauna/ok22.txt", fileChannel);

        ByteBuffer byteBuffer = clientService.downloadFile("/shauna/ok22.txt");

        FileChannel fileChannel2 = new RandomAccessFile("F:\\java项目\\ShaunaDfsTmp\\jdk-8u201-windows-x64.exe",
                "rw").getChannel();

        fileChannel2.write(byteBuffer);

        fileChannel.close();
        fileChannel2.close();

//        boolean b1 = clientService.rmDir("/shauna");
//        boolean b1 = clientService.rmFile("/shauna/ok22.txt");
//        System.out.println(b1);
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
        File file = new File("F:\\java项目\\ShaunaDfsTmp\\ShaunaImage-bak.dat");
        file.deleteOnExit();
    }
}