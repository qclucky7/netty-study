package com.netty.study.loadbalance.loadbalance;

/**
 * @author WangChen
 * @since 2020-12-11 10:19
 **/
public interface Invoker {

    int getWeight();

    String getHostName();
}
