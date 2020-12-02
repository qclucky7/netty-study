package com.netty.study.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author WangChen
 * @since 2020-12-02 14:06
 **/
public class NettyRpcProxyFactory {

    public static <T> T getInstance(Class<T> clazz, InvocationHandler methodHandler){
        return getInstance(Thread.currentThread().getContextClassLoader(), clazz, methodHandler);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(ClassLoader classLoader, Class<T> clazz, InvocationHandler methodHandler){
        return (T)Proxy.newProxyInstance(classLoader, new Class<?>[]{clazz}, methodHandler);
    }

}
