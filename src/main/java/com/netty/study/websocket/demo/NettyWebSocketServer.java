package com.netty.study.websocket.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * @author WangChen
 * @since 2020-12-04 11:30
 **/
public class NettyWebSocketServer {

    private static ConcurrentMap<String, ChannelHandlerContext> cacheMap = new ConcurrentHashMap<>();

    private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);


    public static void main(String[] args) {

        scheduledThreadPool.scheduleAtFixedRate(() -> {
            for (ChannelHandlerContext channelHandlerContext : cacheMap.values()) {
                channelHandlerContext.writeAndFlush(new TextWebSocketFrame("推送消息: 服务器消息！ 日期:" + LocalDate.now().toString()));
            }
        }, 5, 30, TimeUnit.SECONDS);



        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //第一次握手请求时由Http协议承载，使用Http的编码器和解码器
                            pipeline.addLast(new HttpServerCodec());
                            //是以块方式写，支持异步发送大的码流（如大文件的传输），但不占用过多的内存
                            pipeline.addLast(new ChunkedWriteHandler());
                            /**
                             * Http数据在传输过程中是分段的，HttpObjectAggregator可以将多个段聚合
                             * 这就是为什么当浏览器发送大量数据时就会发送多次Http请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            /**
                             * 对于websocket数据是以帧（frame）的形式传递
                             * 可以看到WebSocketFrame下面有6个子类
                             * 浏览器请求时:ws://localhost:7000/hello 表示请求的uri，（与页面中websocket中url一样）
                             * WebSocketServerProtocolHandler的核心功能是将Http协议升级成ws协议（是通过状态码101），保持长连接
                             */
                            pipeline.addLast(new WebSocketHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                        }
                    });
            ChannelFuture future = bootstrap.bind(7000).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    static class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

            if (msg instanceof FullHttpRequest) { // 传统的HTTP接入
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





}
