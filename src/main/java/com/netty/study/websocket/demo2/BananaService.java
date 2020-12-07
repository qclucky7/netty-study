package com.netty.study.websocket.demo2;

import com.netty.study.websocket.demo2.util.CODE;
import com.netty.study.websocket.demo2.util.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author WangChen
 * @since 2020-12-07 17:06
 **/
public class BananaService implements BananaCallBack {
    private static final Logger LOG = Logger.getLogger(BananaService.class.getName());

    public static final Map<String, BananaCallBack> bananaWatchMap = new ConcurrentHashMap<String, BananaCallBack>(); // <requestId, callBack>

    private ChannelHandlerContext ctx;
    private String name;

    public BananaService(ChannelHandlerContext ctx, String name) {
        this.ctx = ctx;
        this.name = name;
    }

    public static boolean register(String requestId, BananaCallBack callBack) {
        if (Strings.isNotBlank(requestId) || bananaWatchMap.containsKey(requestId)) {
            return false;
        }
        bananaWatchMap.put(requestId, callBack);
        return true;
    }

    public static boolean logout(String requestId) {
        if (Strings.isNotBlank(requestId) || !bananaWatchMap.containsKey(requestId)) {
            return false;
        }
        bananaWatchMap.remove(requestId);
        return true;
    }

    @Override
    public void send(Request request) throws Exception {
        if (this.ctx == null || this.ctx.isRemoved()) {
            throw new Exception("尚未握手成功，无法向客户端发送WebSocket消息");
        }
        this.ctx.channel().write(new TextWebSocketFrame(request.toJson()));
        this.ctx.flush();
    }


    /**
     * 通知所有机器有机器下线
     * @param requestId
     */
    public static void notifyDownline(String requestId) {
        BananaService.bananaWatchMap.forEach((reqId, callBack) -> { // 通知有人下线
            Request serviceRequest = new Request();
            serviceRequest.setServiceId(CODE.downline.code);
            serviceRequest.setRequestId(requestId);
            try {
                callBack.send(serviceRequest);
            } catch (Exception e) {
                LOG.warning("回调发送消息给客户端异常");
            }
        });
    }

    public String getName() {
        return name;
    }

}

