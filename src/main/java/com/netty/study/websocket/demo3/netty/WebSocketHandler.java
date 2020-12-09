package com.netty.study.websocket.demo3.netty;

import com.netty.study.websocket.demo3.SessionHolder;
import com.netty.study.websocket.demo3.utils.Channels;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
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
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {

        if (msg instanceof TextWebSocketFrame){
            TextWebSocketFrame msgs = (TextWebSocketFrame)msg;
            System.out.println("服务器收到消息" + msgs.text());
            //响应
            for (ChannelHandlerContext channelHandlerContext : SessionHolder.getInstance().getAll()) {
                if (Channels.notEquals(channelHandlerContext, ctx)){
                    Channels.writeAndFlush(ctx, new TextWebSocketFrame("群发消息:" + LocalDateTime.now() + ": " +msgs.text()));
                }
            }
            Channels.writeAndFlush(ctx, new TextWebSocketFrame("服务器时间" + LocalDateTime.now() + msgs.text()));
        }

        if (msg instanceof BinaryWebSocketFrame){
            BinaryWebSocketFrame bytes = (BinaryWebSocketFrame)msg;
            ByteBuf content = bytes.content();
            content.markReaderIndex();
            int flag = content.readInt();
            System.out.println("标志位 :" + flag);
            content.resetReaderIndex();
//
//                ByteBuf byteBuf = Unpooled.directBuffer(msg.content().capacity());
//                byteBuf.writeBytes(msg.content());

            //ctx.writeAndFlush(new BinaryWebSocketFrame(byteBuf));

            //int 4个字节  一个字节占8位   2的32次方

            content = content.skipBytes(content.readInt());

            String path = "D:\\netty-study\\src\\main\\resources\\file\\";
            //不支持拷贝
            //Files.write(Paths.get(path + "netty.jpeg"), byteBuf.array());
            try(FileOutputStream fileOutputStream = new FileOutputStream(new File(path + "netty.jpeg"))){
                byte[] bytes1 = new byte[content.readableBytes()];
                while (content.isReadable()){
                    content.readBytes(bytes1);
                    fileOutputStream.write(bytes1);
                }
            }
        }

    }

    /**
     * 当web客户端连接后，触发方法
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        if (ctx instanceof WebSocketFrame){
            //id表示唯一的值，LongText是唯一的，ShortText不是唯一的
            SessionHolder.getInstance().register(Channels.getId(ctx), ctx);
            System.out.println("handlerAdd被调用" + Channels.getId(ctx));
            System.out.println("handlerAdd被调用" + Channels.getId(ctx));
        }

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

