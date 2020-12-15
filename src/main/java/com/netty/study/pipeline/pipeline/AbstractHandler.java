package com.netty.study.pipeline.pipeline;

import com.netty.study.pipeline.monitor.Monitor;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author WangChen
 * @since 2020-12-10 10:25
 **/
public abstract class AbstractHandler implements Handler, Comparator<AbstractHandler>{

    private int order;
    private final boolean inbound;
    private final boolean outbound;
    volatile AbstractHandler next;
    volatile AbstractHandler prev;
    private List<Monitor> monitors;

    public AbstractHandler(boolean inbound, boolean outbound) {
        this.inbound = inbound;
        this.outbound = outbound;
        this.monitors = new ArrayList<>();
        order = 0;
    }

    public AbstractHandler(boolean inbound, boolean outbound, Monitor... monitor) {
        this.inbound = inbound;
        this.outbound = outbound;
        monitors = Arrays.asList(monitor);
    }

    @Override
    public void invoke(Object msg) {
        this.invoke0(msg);
        if (CollectionUtils.isEmpty(monitors)){
            return;
        }
        for (Monitor monitor : monitors) {
            monitor.callback(this);
        }
    }

    @Override
    public boolean isInbound() {
        return inbound;
    }

    @Override
    public boolean isOutbound() {
        return outbound;
    }


    static void invokeInbound(AbstractHandler handler, Object msg){
        if (handler == null){
            return;
        }
        handler.invoke(msg);
        handler.fireInboundHandler(msg);
    }


    static void invokeOutbound(AbstractHandler handler, Object msg){
        if (handler == null){
            return;
        }
        handler.invoke(msg);
        handler.fireOutboundHandler(msg);
    }

    /**
     * 执行下一个节点
     * @param msg
     */
    private void fireInboundHandler(Object msg){
        invokeInbound(findContextInbound(), msg);
    }

    /**
     * 执行上一个节点
     * @param msg
     */
    private void fireOutboundHandler(Object msg){
        invokeOutbound(findContextOutbound(), msg);
    }

    private AbstractHandler findContextInbound() {
        AbstractHandler ctx = this;
        do {
            ctx = ctx.next;
            if (ctx == null){
                return null;
            }
        } while(!ctx.inbound);

        return ctx;
    }

    private AbstractHandler findContextOutbound() {
        AbstractHandler ctx = this;

        do {
            ctx = ctx.prev;
            if (ctx == null){
                return null;
            }
        } while(!ctx.outbound);

        return ctx;
    }

    public AbstractHandler registerListener(Monitor ... monitor){
        monitors.addAll(Arrays.asList(monitor));
        return this;
    }

    @Override
    public int compare(AbstractHandler o1, AbstractHandler o2) {
        return o1.order - o2.order;
    }

    abstract void invoke0(Object msg);
}
