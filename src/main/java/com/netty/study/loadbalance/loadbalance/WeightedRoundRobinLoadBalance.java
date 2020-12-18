package com.netty.study.loadbalance.loadbalance;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WangChen
 * @since 2020-12-16 09:24
 **/
public class WeightedRoundRobinLoadBalance extends AbstractLoadBalance{

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    protected Invoker doSelect(List<Invoker> invokers) {

        Iterator<Invoker> iterator = invokers.iterator();

        while (iterator.hasNext()){

        }

        return null;
    }
}
