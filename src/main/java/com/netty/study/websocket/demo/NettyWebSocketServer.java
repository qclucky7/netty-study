package com.netty.study.websocket.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            pipeline.addLast(new WebSocketHandler());
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

    static class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

        private WebSocketServerHandshaker webSocketServerHandshaker;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {

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
            //id表示唯一的值，LongText是唯一的，ShortText不是唯一的
            cacheMap.put(ctx.channel().id().asLongText(), ctx);
            System.out.println("handlerAdd被调用" + ctx.channel().id().asLongText());
            System.out.println("handlerAdd被调用" + ctx.channel().id().asShortText());
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
    }


}
