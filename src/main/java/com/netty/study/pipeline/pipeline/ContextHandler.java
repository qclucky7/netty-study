package com.netty.study.pipeline.pipeline;

import com.netty.study.pipeline.monitor.Monitor;

/**
 * @author WangChen
 * @since 2020-12-10 10:07
 **/
public class ContextHandler extends AbstractHandler{

    public ContextHandler(boolean inbound, boolean outbound) {
        super(inbound, outbound);
    }

    public ContextHandler(boolean inbound, boolean outbound, Monitor... monitor) {
        super(inbound, outbound, monitor);
    }

    @Override
    public void invoke0(Object msg) {
        System.out.println("内容处理器");
    }
}
