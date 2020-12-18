package com.netty.study.loadbalance.loadbalance;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author WangChen
 * @since 2020-12-11 14:15
 **/
public class SimpleRoundRobinLoadBalance extends AbstractLoadBalance{

    private int index = 0;
    private final Object lock = new Object();

    @Override
    protected Invoker doSelect(List<Invoker> invokers) {

        synchronized (lock){
            if (index == invokers.size()){
                index = 0;
            }
            Invoker invoker = invokers.get(index);
            index++;
            return invoker;
        }

    }

}
