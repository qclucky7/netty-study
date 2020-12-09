package com.netty.study.websocket.demo3;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

/**
 * @author WangChen
 * @since 2020-12-09 08:56
 **/
public interface Operate {

    void register(String uniqueId, ChannelHandlerContext channelHandlerContext);


    ChannelHandlerContext get(String uniqueId);


    void clear(String uniqueId);


    Collection<ChannelHandlerContext> getAll();
}
