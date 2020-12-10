package com.netty.study.pipeline.pipeline;

import com.netty.study.pipeline.monitor.Monitor;

/**
 * @author WangChen
 * @since 2020-12-10 10:10
 **/
public class EncoderHandler extends AbstractHandler{

    public EncoderHandler(boolean inbound, boolean outbound) {
        super(inbound, outbound);
    }

    public EncoderHandler(boolean inbound, boolean outbound, Monitor... monitor) {
        super(inbound, outbound, monitor);
    }

    @Override
    public void invoke0(Object msg) {
        System.out.println("编码处理器");
    }
}
