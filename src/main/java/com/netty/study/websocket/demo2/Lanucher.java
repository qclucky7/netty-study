package com.netty.study.websocket.demo2;

/**
 * @author WangChen
 * @since 2020-12-07 16:50
 **/
public class Lanucher {

    public static void main(String[] args) throws Exception {
        // 启动WebSocket
        new WebSocketServer().run(WebSocketServer.WEBSOCKET_PORT);
    }

}
