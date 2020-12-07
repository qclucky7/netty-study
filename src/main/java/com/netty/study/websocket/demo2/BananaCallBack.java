package com.netty.study.websocket.demo2;

import com.netty.study.websocket.demo2.util.Request;

/**
 * @author WangChen
 * @since 2020-12-07 16:54
 **/
public interface BananaCallBack {

    // 服务端发送消息给客户端
    void send(Request request) throws Exception;
}
