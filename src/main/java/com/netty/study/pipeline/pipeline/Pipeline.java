package com.netty.study.pipeline.pipeline;

import io.netty.channel.ChannelHandler;

/**
 * @author WangChen
 * @since 2020-12-10 11:09
 **/
public interface Pipeline {


    /**
     * 添加last处理器
     * @see io.netty.channel.DefaultChannelPipeline#addLast(ChannelHandler)
     * @param handler
     * @return
     */
    Pipeline addLast(AbstractHandler handler);


    /**
     * 添加first处理器
     * @see io.netty.channel.DefaultChannelPipeline#addFirst(ChannelHandler)
     * @param handler
     * @return
     */
    Pipeline addFirst(AbstractHandler handler);


    /**
     * 执行入栈
     * @see io.netty.channel.DefaultChannelPipeline#fireChannelRead(Object msg)
     * @param msg
     */
    void invokeInbound(Object msg);


    /**
     *
     * 执行出栈
     * @see io.netty.channel.DefaultChannelPipeline#fireChannelRead(Object msg)
     * @param msg
     */
    void invokeOutbound(Object msg);
}
