package com.netty.study.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author WangChen
 * @since 2020-12-02 14:34
 **/
public class NettyConsumer {

    public static void startServer(String hostName) {
        startClient(hostName, 8080);
    }

    public static void startClient(String hostName, int port) {
        startClient0(hostName, port);
    }

    private static void startClient0(String hostName, int port) {



    }

}
