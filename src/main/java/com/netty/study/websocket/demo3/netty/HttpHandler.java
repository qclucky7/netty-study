package com.netty.study.websocket.demo3.netty;

import com.netty.study.websocket.demo3.SessionHolder;
import com.netty.study.websocket.demo3.utils.Channels;
import com.netty.study.websocket.demo3.utils.UrlUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.junit.platform.commons.util.CollectionUtils;
import sun.misc.Contended;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN;

/**
 * @author WangChen
 * @since 2020-12-08 15:46
 **/
public class HttpHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            System.out.println("进http请求了!!");
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else {
            //责任链模式 和过滤器拦截器实现一致
            ctx.fireChannelRead(((WebSocketFrame) msg).retain());
        }

    }

    /**
     * 处理Http请求，完成WebSocket握手<br/>
     * 注意：WebSocket连接第一次请求使用的是Http
     *
     * @param ctx
     * @param request
     * @throws Exception
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 如果HTTP解码失败，返回HTTP异常
        if (!request.decoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
            System.out.println("解码失败!");
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
            return;
        }

        request.method().name(); //获取请求方法
        request.uri(); //获取请求URI
        request.protocolVersion().text(); //获取HTTP协议版本

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());

        System.out.println(queryStringDecoder.parameters().toString());
        System.out.println(queryStringDecoder.path());
        System.out.println(queryStringDecoder.rawPath());
        System.out.println(queryStringDecoder.rawQuery());
        System.out.println(queryStringDecoder.toString());
        System.out.println(queryStringDecoder.uri());

        //Map<String, String> param = UrlUtils.getParam(request.uri());

        Map<String, List<String>> parameters = queryStringDecoder.parameters();


        if (parameters.get("token") == null || parameters.get("token").isEmpty() || parameters.get("token").get(0).isEmpty()) {
            System.out.println("验证不通过");

            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.UNAUTHORIZED));
            return;
        }

        System.out.println("处理http请求！！！");

        //协议升级
        WebSocketServerHandshaker webSocketServerHandshaker =
                new WebSocketServerHandshakerFactory("ws://localhost:7000", null, false)
                        .newHandshaker(request);

        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            System.out.println("不支持的版本");
            return;
        }

        // 向客户端发送websocket握手,完成握手
        System.out.println("完成握手");
        webSocketServerHandshaker.handshake(ctx.channel(), request);
        SessionHolder.getInstance().register(Channels.getId(ctx), ctx);

    }

    /**
     * Http返回
     *
     * @param ctx
     * @param request
     * @param response
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        // 返回应答给客户端
        if (response.status() != HttpResponseStatus.OK) {
            ByteBuf buf = Unpooled.copiedBuffer("Unauthorized", CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }
        //允许跨域访问
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT,DELETE");

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(response, ctx.newPromise());
        if (!HttpUtil.isKeepAlive(request) || response.status() != HttpResponseStatus.OK) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}

