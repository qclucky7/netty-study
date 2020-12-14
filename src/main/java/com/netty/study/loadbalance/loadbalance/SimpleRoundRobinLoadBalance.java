package com.netty.study.loadbalance.loadbalance;

import java.util.List;

/**
 * @author WangChen
 * @since 2020-12-11 14:15
 **/
public class SimpleRoundRobinLoadBalance extends AbstractLoadBalance{

    @Override
    protected Invoker doSelect(List<Invoker> invokers) {
        return null;
    }

}
