package top.shauna.dfs.monitor.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import top.shauna.dfs.monitor.controller.KingController;
import top.shauna.dfs.monitor.util.ResponseUtil;

import java.nio.charset.Charset;

public class MonitorHnadler extends SimpleChannelInboundHandler<HttpObject> {
    private KingController kingController;

    public MonitorHnadler(){
        kingController = new KingController();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if (httpObject instanceof HttpRequest){
            HttpRequest request = (HttpRequest) httpObject;

            String body="";
            String respLine="";
            try {
                body = ResponseUtil.getBody(request);
                respLine = ResponseUtil.getRespLine(200);
            }catch (Exception e){
                e.printStackTrace();
                body = e.toString();
                respLine = ResponseUtil.getRespLine(500);
            }finally {
                String respHeader = ResponseUtil.getRespHeader(body);
                String res = respLine + respHeader + body;
                ByteBuf byteBuf = Unpooled.copiedBuffer(
                        res.toCharArray(),
                        Charset.forName("UTF-8"));
                channelHandlerContext.channel().writeAndFlush(byteBuf);
            }
        }
    }
}
