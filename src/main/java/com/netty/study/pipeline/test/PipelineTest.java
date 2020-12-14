package com.netty.study.pipeline.test;

import com.netty.study.pipeline.pipeline.*;
import org.junit.jupiter.api.Test;

/**
 * @author WangChen
 * @since 2020-12-10 11:18
 **/
public class PipelineTest {

    @Test
    public void test() {

        Pipeline pipeline = PipelineFactory.newInstance();

        pipeline.addLast(new DecoderHandler(true, false)
                .registerListener(handler -> {
                    System.out.println("DecoderHandler callback:" + handler.isInbound());
                    System.out.println("DecoderHandler callback:" + handler.isOutbound());
                }));

        pipeline.addLast(new ContextHandler(true, false)
                .registerListener(handler -> {
                    System.out.println("ContextHandler callback:" + handler.isInbound());
                    System.out.println("ContextHandler callback:" + handler.isOutbound());
                }));

        pipeline.addLast(new EncoderHandler(false, true)
                .registerListener(handler -> {
                    System.out.println("EncoderHandler callback:" + handler.isInbound());
                    System.out.println("EncoderHandler callback:" + handler.isOutbound());
                }));

        pipeline.addFirst(new DecoderHandler(true, false)
                .registerListener(handler -> {
                    System.out.println("DecoderHandler callback: 添加到第一个" + handler.isInbound());
                    System.out.println("DecoderHandler callback: 添加到第一个" + handler.isOutbound());
                }));

        pipeline.invokeInbound("读取消息");
        System.out.println("-----------------------------------------------");
        pipeline.invokeOutbound("响应消息");


    }
}
