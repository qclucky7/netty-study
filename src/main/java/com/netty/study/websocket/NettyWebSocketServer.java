package com.netty.study.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.time.LocalDateTime;

/**
 * @author WangChen
 * @since 2020-12-04 11:30
 **/
public class NettyWebSocketServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
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
                            pipeline.addLast(new HttpObjectAggregator(8192));
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

    static class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            System.out.println("服务器收到消息" + msg.text());
            //响应
            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间" + LocalDateTime.now() + msg.text()));
        }

        /**
         * 当web客户端连接后，触发方法
         */
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            //id表示唯一的值，LongText是唯一的，ShortText不是唯一的
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
            System.out.println("异常发生" + cause.getMessage());
            ctx.close();

        }
    }

}
