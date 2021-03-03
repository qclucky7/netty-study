package com.netty.study.websocket.demo3.netty;

import com.netty.study.websocket.demo3.SessionHolder;
import com.netty.study.websocket.demo3.utils.Channels;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

import static io.netty.handler.codec.stomp.StompHeaders.HEART_BEAT;

/**
 * @author WangChen
 * @since 2020-12-08 15:46
 **/
public class TcpHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到pyClient信息:" + msg);

    }

    /**
     * 当web客户端连接后，触发方法
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("py client链接了........");
    }

    /**
     * 当web客户端连接断开后，触发方法
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved被调用" + Channels.getId(ctx));
        SessionHolder.getInstance().clear(Channels.getId(ctx));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionHolder.getInstance().clear(Channels.getId(ctx));
        Channels.close(ctx);
        throw new Exception(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("触发事件");
        System.out.println("obj = " + evt.toString());
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                System.out.println("写空闲！！！");
                ctx.writeAndFlush(HEART_BEAT);
                ctx.close();
                SessionHolder.getInstance().clear(Channels.getId(ctx));
            } else if (state == IdleState.READER_IDLE){
                // read heartbeat to server
                System.out.println("读空闲！！");
                ctx.writeAndFlush(HEART_BEAT);
                ctx.close();
                SessionHolder.getInstance().clear(Channels.getId(ctx));
            } else if (state == IdleState.ALL_IDLE){
                // write and read heartbeat to server
                System.out.println("读写空闲空闲！！");
                ctx.writeAndFlush(HEART_BEAT);
                ctx.close();
                SessionHolder.getInstance().clear(Channels.getId(ctx));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}

