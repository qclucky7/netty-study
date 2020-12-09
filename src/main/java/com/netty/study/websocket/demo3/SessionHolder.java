package com.netty.study.websocket.demo3;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author WangChen
 * @since 2020-12-08 15:59
 **/
public class SessionHolder implements Operate{

    private static final ConcurrentMap<String, ChannelHandlerContext> CHANNELS = new ConcurrentHashMap<>();

    private static SessionHolder INSTANCE = new SessionHolder();

    public static SessionHolder getInstance(){
        return INSTANCE;
    }

    @Override
    public void register(String uniqueId, ChannelHandlerContext channelHandlerContext){
        CHANNELS.put(uniqueId, channelHandlerContext);
    }


    @Override
    public ChannelHandlerContext get(String uniqueId){
        return CHANNELS.get(uniqueId);
    }


    @Override
    public void clear(String uniqueId){
        System.out.println("触发清理 id=" + uniqueId);
        CHANNELS.remove(uniqueId);
    }


    @Override
    public Collection<ChannelHandlerContext> getAll(){
        return CHANNELS.values();
    }
}
