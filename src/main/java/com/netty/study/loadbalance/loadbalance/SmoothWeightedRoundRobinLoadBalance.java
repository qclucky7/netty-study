package com.netty.study.loadbalance.loadbalance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WangChen
 * @since 2020-12-11 14:27
 **/
public class SmoothWeightedRoundRobinLoadBalance extends AbstractLoadBalance{

    private static ConcurrentMap<String, ServerNode> map = new ConcurrentHashMap<>();

    @Override
    protected Invoker doSelect(List<Invoker> invokers) {

        Invoker curInvoker = null;
        ServerNode serverNodeSelected = null;
        int maxWeight = Integer.MIN_VALUE;
        int totalWeight = 0;

        for (Invoker invoker : invokers) {

            String hostName = invoker.getHostName();
            int weight = invoker.getWeight();

            ServerNode serverNode = map.computeIfAbsent(invoker.getHostName(), x -> new ServerNode(hostName, weight));

            // 增加各自的 currentWeight
            serverNode.increaseCurrentWeight();
            if (serverNode.getCurrentWeight() > maxWeight) {
                maxWeight = serverNode.getCurrentWeight();
                curInvoker = invoker;
                serverNodeSelected = serverNode;
            }
            totalWeight += serverNode.getWeight();
        }

        if (serverNodeSelected != null) {
            // 被选中的节点，currentWeight 需要减去所有 weight 之和
            serverNodeSelected.selected(totalWeight);
            return curInvoker;
        }
        // should not happen here
        return invokers.get(0);
    }


    static class ServerNode{

        private String hostname;
        /** 设置的weight */
        private int weight;
        /** 当前weight */
        private AtomicInteger currentWeight;

        public ServerNode(String hostname, int weight) {
            this.hostname = hostname;
            this.weight = weight;
            this.currentWeight = new AtomicInteger(0);
        }

        public void selected(int total) {
            currentWeight.addAndGet(-1 * total);
        }

        public void increaseCurrentWeight() {
            currentWeight.addAndGet(weight);
        }

        public int getCurrentWeight() {
            return currentWeight.get();
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return "ServerNode{" +
                    "hostname='" + hostname + '\'' +
                    ", weight=" + weight +
                    ", currentWeight=" + currentWeight +
                    '}';
        }
    }
}
