package com.netty.study.rpc.server;

/**
 * @ClassName NettyServerStart
 * @description:
 * @author: WangChen
 * @create: 2020-03-07 17:55
 **/
public class NettyServerStar {

    public static void main(String[] args) {
        NettyServer.startServer("127.0.0.1",8888);
    }
}
