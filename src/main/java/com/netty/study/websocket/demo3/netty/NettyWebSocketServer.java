package com.netty.study.websocket.demo3.netty;

import com.netty.study.websocket.demo3.SessionHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.*;

/**
 * @author WangChen
 * @since 2020-12-04 11:30
 **/
@Component
public class NettyWebSocketServer implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(NettyWebSocketServer.class);

    private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    private final ExecutorService nettyServerThread = Executors.newSingleThreadExecutor();
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    @Override
    public void afterPropertiesSet() throws Exception {

        scheduledThreadPool.scheduleAtFixedRate(() -> {
            for (ChannelHandlerContext channelHandlerContext : SessionHolder.getInstance().getAll()) {
                channelHandlerContext.writeAndFlush(new TextWebSocketFrame("推送消息: 服务器消息！ 日期:" + LocalDate.now().toString()));
            }
        }, 5, 5, TimeUnit.SECONDS);

        nettyServerThread.execute(() -> {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

            log.info("NettyWebSocketServer staring....");

            /**
             * option 针对bossGroup
             * childOption 针对workerGroup
             */
            try{
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        //配置TCP参数，握手字符串长度设置
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        //开启心跳包活机制，就是客户端、服务端建立连接处于ESTABLISHED状态，超过2小时没有交流，机制会被启动
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        //配置固定长度接收缓存区分配器
                        .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(592048))
                        //将小的数据包包装成更大的帧进行传送，提高网络的负载 TCP_NODELAY 是取消 TCP 的确认延迟机制，相当于禁用了 Negale 算法
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                /**
                                 * 心跳检测
                                 * readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接 ，客户端未发送信息到服务端,那么会触发channel的读空闲。
                                 * writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接，服务端未发送信息到客户端,那么会触发channel的写空闲。
                                 * allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接
                                 *
                                 */
                                pipeline.addLast(new IdleStateHandler(60,60,30));
                                //第一次握手请求时由Http协议承载，使用Http的编码器和解码器
                                //pipeline.addLast(new HttpServerCodec());
                                /**
                                 * Http数据在传输过程中是分段的，HttpObjectAggregator可以将多个段聚合
                                 * 这就是为什么当浏览器发送大量数据时就会发送多次Http请求
                                 */
                                //pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                                //是以块方式写，支持异步发送大的码流（如大文件的传输），但不占用过多的内存
                                pipeline.addLast(new StringEncoder());
                                pipeline.addLast(new StringDecoder());
                                pipeline.addLast(new ChunkedWriteHandler());
                                pipeline.addLast(new TcpHandler());
                                //pipeline.addLast(new HttpHandler());
                                /**
                                 * 对于websocket数据是以帧（frame）的形式传递
                                 * 可以看到WebSocketFrame下面有6个子类
                                 * 浏览器请求时:ws://localhost:7000/hello 表示请求的uri，（与页面中websocket中url一样）
                                 * WebSocketServerProtocolHandler的核心功能是将Http协议升级成ws协议（是通过状态码101），保持长连接
                                 */
                                //pipeline.addLast(new WebSocketHandler());
                                //pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            }
                        });
                channelFuture = bootstrap.bind(7000).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }

            log.info("NettyWebSocketServer start success !");
        });

    }


    @Override
    public void destroy() throws Exception {

        log.info("[NettyWebSocketServer] destroy start ...");

        channelFuture.channel().close();
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();

        try {
            bossGroupFuture.await();
            workerGroupFuture.await();
        } catch (InterruptedException ignore) {
            log.error("[NettyWebSocketServer] destroy error = {}", ignore.toString());
        }

        log.info("[NettyWebSocketServer] destroy success！");

    }





}
