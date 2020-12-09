package com.netty.study.websocket.demo3.utils;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author WangChen
 * @since 2020-12-09 10:12
 **/
public class Channels {

    public static void close(ChannelHandlerContext channelHandlerContext){
        if (null == channelHandlerContext){
            return;
        }
        channelHandlerContext.close();
    }

    public static String getId(ChannelHandlerContext channelHandlerContext){
        return channelHandlerContext == null ? null : channelHandlerContext.channel().id().asLongText();
    }

    public static boolean equals(ChannelHandlerContext channelHandlerContext0, ChannelHandlerContext channelHandlerContext){
        return channelHandlerContext0 != null && channelHandlerContext != null &&
                channelHandlerContext0.channel().id().asLongText().equals(channelHandlerContext.channel().id().asLongText());
    }

    public static boolean notEquals(ChannelHandlerContext channelHandlerContext0, ChannelHandlerContext channelHandlerContext){
        return !equals(channelHandlerContext0, channelHandlerContext);
    }

    public static ChannelFuture writeAndFlush(ChannelHandlerContext channelHandlerContext, Object object){
        if (null == channelHandlerContext){
            return null;
        }
        return channelHandlerContext.writeAndFlush(object);
    }

}
