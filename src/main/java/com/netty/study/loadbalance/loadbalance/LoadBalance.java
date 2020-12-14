package com.netty.study.loadbalance.loadbalance;

import java.util.List;

/**
 * @author WangChen
 * @since 2020-12-11 10:21
 **/
public interface LoadBalance {

    /**
     * 选择invoker  详情看dubbo
     * @param invokers
     * @return
     * @throws Exception
     */
    Invoker select(List<Invoker> invokers) throws Exception;
}
