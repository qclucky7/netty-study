package com.netty.study.springstrategymodel;

import org.springframework.stereotype.Component;

/**
 * @author WangChen
 * @since 2021-02-01 13:47
 **/
@Component
public class WeChatStrategy implements EventStrategy{

    @Override
    public SourceType getSource() {
        return SourceType.WECHATE;
    }

    @Override
    public void on() {
        System.out.println("WeChatStrategy 执行");
    }
}
