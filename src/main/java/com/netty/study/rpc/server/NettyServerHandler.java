package com.netty.study.rpc.server;

import com.netty.study.rpc.protocol.DubboRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

/**
 * @ClassName NettyServerHandler
 * @description:
 * @author: WangChen
 * @create: 2020-03-07 17:43
 **/
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        System.out.println("[NettyServerHandler] channelRead msg = " + msg);

        DubboRequest dubboRequest = (DubboRequest) msg;

        // 1. 根据类名返回对象
        Object target = this.getInstenceByInterfaceClass(dubboRequest.getInterfaceClass());
        // 2. 获取方法名
        String methodName = dubboRequest.getMethodName();
        // 3. 获取方法参数类型
        // 4. 获取方法
        Method method = target.getClass().getMethod(methodName, dubboRequest.getParamTypes());
        // 5. 获取参数值
        //调用方法 获取返回值
        Object res = method.invoke(target, dubboRequest.getArgs());

        System.out.println("[NettyServerHandler] response = " + res);
        // 写回给调用端
        ctx.writeAndFlush(res);



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.toString());
        System.out.println("[NettyServerHandler] client close");
        //ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 根据接口返回对应的实例
     * @param clazz
     * @return
     */
    private Object getInstenceByInterfaceClass(Class<?> clazz) {
        if (IUserFacade.class.equals(clazz)) {
            return new UserFacade();
        }
        return null;
    }

}
