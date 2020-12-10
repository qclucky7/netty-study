package com.netty.study.pipeline.pipeline;

/**
 * @author WangChen
 * @since 2020-12-10 10:07
 **/
public interface Handler {

    /**
     * 模拟netty pipeline处理
     */
    void invoke(Object msg);

    /**
     * @see io.netty.channel.AbstractChannelHandlerContext
     * netty 里面inboud和oubound确定入站和出站有哪些处理器处理,  出站和入站公用一个pipeline
     * @return
     */
    boolean isInbound();

    boolean isOutbound();
}
