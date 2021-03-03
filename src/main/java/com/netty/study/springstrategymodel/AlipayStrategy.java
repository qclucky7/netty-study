package com.netty.study.springstrategymodel;

import org.springframework.stereotype.Component;

/**
 * @author WangChen
 * @since 2021-02-01 13:46
 **/
@Component
public class AlipayStrategy implements EventStrategy{

    @Override
    public SourceType getSource() {
        return SourceType.ALIPAY;
    }

    @Override
    public void on() {
        System.out.println("AlipayStrategy 执行");
    }
}
