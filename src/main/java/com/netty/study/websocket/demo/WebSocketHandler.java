package com.netty.study.websocket.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author WangChen
 * @since 2020-12-08 15:46
 **/
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private static ConcurrentMap<String, ChannelHandlerContext> cacheMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            System.out.println("进http请求了!!");
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }

        if (msg instanceof TextWebSocketFrame){
            TextWebSocketFrame msgs = (TextWebSocketFrame)msg;
            System.out.println("服务器收到消息" + msgs.text());
            //响应
            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间" + LocalDateTime.now() + msgs.text()));
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
            cacheMap.put(ctx.channel().id().asLongText(), ctx);
            System.out.println("handlerAdd被调用" + ctx.channel().id().asLongText());
            System.out.println("handlerAdd被调用" + ctx.channel().id().asShortText());
        }

    }
    /**
     * 当web客户端连接断开后，触发方法
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved被调用" + ctx.channel().id().asLongText());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cacheMap.remove(ctx.channel().id().asLongText());
        throw new Exception(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("触发事件");
        System.out.println("obj = " + evt.toString());
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 处理Http请求，完成WebSocket握手<br/>
     * 注意：WebSocket连接第一次请求使用的是Http
     * @param ctx
     * @param request
     * @throws Exception
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 如果HTTP解码失败，返回HTTP异常
        if (!request.decoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
            System.out.println("解码失败!");
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        System.out.println("处理http请求！！！");

        // 正常WebSocket的Http连接请求，构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:9090/websocket", null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) { // 无法处理的websocket版本
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            System.out.println("不支持的版本");
        } else { // 向客户端发送websocket握手,完成握手
            System.out.println("完成握手");
            handshaker.handshake(ctx.channel(), request);
            cacheMap.put(ctx.channel().id().asLongText(), ctx);
        }
    }

    /**
     * Http返回
     * @param ctx
     * @param request
     * @param response
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        // 返回应答给客户端
        if (response.status() != HttpResponseStatus.OK) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(response);
        if (!HttpUtil.isKeepAlive(request) || response.status() != HttpResponseStatus.OK) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}

