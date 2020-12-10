package com.netty.study.pipeline.pipeline;

/**
 * @author WangChen
 * @since 2020-12-10 10:08
 **/
public class DecoderHandler extends AbstractHandler{


    public DecoderHandler(boolean inbound, boolean outbound) {
        super(inbound, outbound);
    }

    @Override
    public void invoke0(Object msg) {
        System.out.println("解码处理器");
    }


}
