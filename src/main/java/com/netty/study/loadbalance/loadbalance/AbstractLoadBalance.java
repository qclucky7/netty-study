package com.netty.study.loadbalance.loadbalance;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author WangChen
 * @since 2020-12-11 09:45
 **/
public abstract class AbstractLoadBalance implements LoadBalance{


    @Override
    public Invoker select(List<Invoker> invokers) throws Exception {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return doSelect(invokers);
    }


    protected abstract Invoker doSelect(List<Invoker> invokers);

    int getWeight(Invoker invoker) {
//        int weight;
//        // Multiple registry scenario, load balance among multiple registries.
//        if (REGISTRY_SERVICE_REFERENCE_PATH.equals(url.getServiceInterface())) {
//            weight = url.getParameter(REGISTRY_KEY + "." + WEIGHT_KEY, DEFAULT_WEIGHT);
//        } else {
//            weight = url.getMethodParameter(invocation.getMethodName(), WEIGHT_KEY, DEFAULT_WEIGHT);
//            if (weight > 0) {
//                long timestamp = invoker.getUrl().getParameter(TIMESTAMP_KEY, 0L);
//                if (timestamp > 0L) {
//                    long uptime = System.currentTimeMillis() - timestamp;
//                    if (uptime < 0) {
//                        return 1;
//                    }
//                    int warmup = invoker.getUrl().getParameter(WARMUP_KEY, DEFAULT_WARMUP);
//                    if (uptime > 0 && uptime < warmup) {
//                        weight = calculateWarmUpWeight((int)uptime, warmup, weight);
//                    }
//                }
//            }
//        }
        return Math.max(invoker.getWeight(), 0);
    }





    /**
     * Calculate the weight according to the uptime proportion of warmup time
     * the new weight will be within 1(inclusive) to weight(inclusive)
     *
     * @param uptime the uptime in milliseconds
     * @param warmup the warmup time in milliseconds
     * @param weight the weight of an invoker
     * @return weight which takes warmup into account
     */
    static int calculateWarmUpWeight(int uptime, int warmup, int weight) {
        int ww = (int) ( uptime / ((float) warmup / weight));
        return ww < 1 ? 1 : (Math.min(ww, weight));
    }


}
