package com.netty.study.rpc.client;

import com.netty.study.rpc.protocol.DubboRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author WangChen
 * @since 2020-12-02 14:32
 **/
public class RpcProxyInvocationHandler implements InvocationHandler {

    private ChannelHandlerContext channelHandlerContext;

    private Object response;

    private static volatile boolean startUp = false;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //System.out.println("proxy=" + proxy + "method=" + method.getName() + "args=" + args);

        DubboRequest dubboRequest = new DubboRequest(proxy.getClass().getInterfaces()[0],method.getName(), method.getParameterTypes(), args);

        System.out.println("[RpcProxyInvocationHandler] dubboRequest = " + dubboRequest.toString());

        if (!startUp){
            initNettyClient();
            startUp = true;
        }

        channelHandlerContext.writeAndFlush(dubboRequest);
        //channelHandlerContext.writeAndFlush("我来了！！！这条消息为什么可以接收？？？？！！！！！");


        //TimeUnit.SECONDS.sleep(3);

        return response;
    }

    private void initNettyClient(){

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //pipeline.addLast(new StringEncoder());
                            //pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new NettyClientHandler());
                        }
                    });
            bootstrap.connect("127.0.0.1", 8888).sync();
            //channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //eventLoopGroup.shutdownGracefully();
        }

    }


    public class NettyClientHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            channelHandlerContext = ctx;
            //ctx.writeAndFlush("客户端消息啊！！！！！！！！！！！！！！！");
            System.out.println("[NettyClientHandler] channelRegistered exec!");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //channelHandlerContext = ctx;
            System.out.println("[NettyClientHandler] channelActive exec!");
            //ctx.writeAndFlush("客户端消息啊！！！！！！！！！！！！！！！");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            response = msg;
            System.out.println("[NettyClientHandler] channelRead msg = " + msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("[NettyClientHandler] exceptionCaught close!");
        }

    }


}
