package com.netty.study.loadbalance;

import com.netty.study.loadbalance.loadbalance.Invoker;

/**
 * @author WangChen
 * @since 2020-12-11 10:19
 **/
public class ServerInvoker implements Invoker {

    private int weight;
    private String name;

    public ServerInvoker(int weight, String name) {
        this.weight = weight;
        this.name = name;
    }

    @Override
    public String getHostName() {
        return name;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "ServerInvoker{" +
                "weight=" + weight +
                ", name='" + name + '\'' +
                '}';
    }
}
