package top.shauna.dfs.test;

import org.junit.Test;
import top.shauna.dfs.bean.Block;
import top.shauna.dfs.block.interfaces.AbstractBlockHandler;
import top.shauna.dfs.block.interfaces.BlockHandler;
import top.shauna.dfs.config.PubConfig;
import top.shauna.dfs.interact.MessageUtil;
import top.shauna.dfs.interact.heartbeat.SoldierHeartBeat;
import top.shauna.dfs.monitor.MonitorProxy;
import top.shauna.dfs.storage.impl.LocalFileStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.Channel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/24 11:37
 * @E-Mail z1023778132@icloud.com
 */
public class Test1 {

    @Test
    public void test1() throws Exception {
        File f = new File("F:\\java项目\\ShaunaDFS\\pom.xml");
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\java项目\\ShaunaDFS\\pom22.xml","rw");
        Channel channel = randomAccessFile.getChannel();
        byte[] a = new byte[]{50,48,49};
        randomAccessFile.seek(randomAccessFile.length());
        randomAccessFile.write(a);
        byte[] bytes = new byte[(int)randomAccessFile.length()];
        randomAccessFile.read(bytes);

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        System.out.println(maxMemory/1024/1024+"  "+freeMemory/1024/1024+"  "+totalMemory/1024/1024);
        System.out.println((double) (freeMemory+maxMemory-totalMemory)/(double) maxMemory);
    }

    @Test
    public void test2() throws Exception {
        LocalFileStorage localFileStorage = LocalFileStorage.getInstance();
        byte[] bytes = {48, 49, 50, 97};
        System.out.println(localFileStorage.isExits("F:\\java项目\\test.txt"));
//        localFileStorage.write("F:\\java项目\\test.txt",bytes);
        localFileStorage.append("F:\\java项目\\test.txt",bytes);
        byte[] read = localFileStorage.read("F:\\java项目\\test.txt");
        String s = new String(read);
        System.out.println(s);
        RandomAccessFile file = new RandomAccessFile("F:\\java项目\\test1.txt", "rw");
        localFileStorage.read("F:\\java项目\\test.txt",file.getChannel());
        File f = new File("F:\\java项目\\root\\test");
        System.out.println(f.exists());
        System.out.println(f.isDirectory());
        f.mkdirs();
    }

    @Test
    public void test3() throws FileNotFoundException {
        RandomAccessFile file = new RandomAccessFile("F:\\java项目\\test1.txt_1.block", "rw");
        MessageDigest md = null;
        byte[] bb = new byte[256*1024*1024];
        for(int i=0;i<bb.length;i++){
            bb[i] = (byte)(i%127);
        }
        long t1 = System.currentTimeMillis();
        try {
            String str = new String(bb);
            md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            String s = new BigInteger(1, md.digest()).toString();
            System.out.println(s);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
    }

    @Test
    public void test4() throws Exception {
        Block block = new Block();
        byte[] content = "你好啊！我是周旭峰！！！！".getBytes();
        String md5 = getMD5(content);
        block.setContent(content);
        block.setFilePath("/root/test/ok/data11");
        block.setMd5(md5);
        block.setPin(1);
        block.setVersion(1L);
        PubConfig instance = PubConfig.getInstance();
        instance.setRootDir("F:\\java项目\\ShaunaDfsTmp");
        BlockHandler abstractBlockHandler = new MonitorProxy().getProxy();
        abstractBlockHandler.write(block);

        System.out.println("====================================");
        Block block2 = new Block();
        block2.setFilePath("/root/test/ok/data11");
        block2.setPin(1);
        block2.setMd5(md5);
        abstractBlockHandler.read(block2);
        System.out.println(new String(block2.getContent()));

        System.out.println("====================================");
        Block block3= new Block();
        block3.setFilePath("/root/test/ok/data11");
        block3.setPin(1);
        block3.setMd5(md5);
        RandomAccessFile file = new RandomAccessFile("F:\\java项目\\test222222.txt", "rw");
        abstractBlockHandler.readAndTransfer(block,file.getChannel());

//        Thread.sleep(15000);
//        MessageUtil.deleteFile(block.getFilePath(),block.getPin().toString());
    }

    private String getMD5(byte[] bb){
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

    @Test
    public void test5() throws Exception {
//        SoldierHeartBeat soldierHeartBeat = new SoldierHeartBeat();
//        System.out.println(soldierHeartBeat.sendHeartBeat(MessageUtil.getHeartBeatRequestBean()));
    }
}
