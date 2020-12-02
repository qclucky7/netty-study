package com.netty.study.rpc.client;

import com.netty.study.rpc.server.IUserFacade;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName Customer
 * @description:
 * @author: WangChen
 * @create: 2020-03-07 17:27
 **/
public class Customer {


    public static void main(String[] args) throws InterruptedException {

        //NettyConsumer.startClient("127.0.0.1", 8080);

        //TimeUnit.SECONDS.sleep(5);

        IUserFacade userFacade = NettyRpcProxyFactory.getInstance(IUserFacade.class, new RpcProxyInvocationHandler());

        String userName = userFacade.getUserName(100L);

        System.out.println("client 收到消息 ! message = " +userName );
    }
}
