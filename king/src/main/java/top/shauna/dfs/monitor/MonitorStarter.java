package top.shauna.dfs.monitor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import top.shauna.dfs.monitor.controller.KingController;
import top.shauna.dfs.monitor.handler.MonitorHnadler;
import top.shauna.dfs.monitor.util.ResponseUtil;
import top.shauna.dfs.starter.Starter;
import top.shauna.dfs.threadpool.CommonThreadPool;

import java.lang.reflect.Method;

/**
 * @Author Shauna.Chou
 * @Date 2020/11/1 14:58
 * @E-Mail z1023778132@icloud.com
 */
public class MonitorStarter implements Starter {
    @Override
    public void onStart() throws Exception {
        addMapper();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>(){

            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();

                    pipeline.addLast(new HttpRequestDecoder());

                    pipeline.addLast("handler",new MonitorHnadler());

                }
            });
            Channel ch = b.bind(8888)//Integer.parseInt(KingPubConfig.getInstance().getMonitorPort()))
                    .sync()
                    .channel();
            CommonThreadPool.threadPool.execute(()->{
                try {
                    ch.closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    bossGroup.shutdownGracefully();
                    workGroup.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMapper() throws NoSuchMethodException {
        KingController kingController = new KingController();

        Method FSInfo = KingController.class.getMethod("FSInfo");
        ResponseUtil.map.put("/fsinfo",new ResponseUtil.Node(FSInfo,kingController));

        Method blocksInfo = KingController.class.getMethod("blocksInfo");
        ResponseUtil.map.put("/blocksinfo",new ResponseUtil.Node(blocksInfo,kingController));

        Method soldiersInfo = KingController.class.getMethod("soldiersInfo");
        ResponseUtil.map.put("/soldiersinfo",new ResponseUtil.Node(soldiersInfo,kingController));

        Method queenInfo = KingController.class.getMethod("queenInfo");
        ResponseUtil.map.put("/queeninfo",new ResponseUtil.Node(queenInfo,kingController));

        Method okk = KingController.class.getMethod("okk",String.class);
        ResponseUtil.map.put("/okk",new ResponseUtil.Node(okk,kingController));
    }
}
