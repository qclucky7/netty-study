package com.netty.study.springeventlisten.user;

import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

/**
 * @author WangChen
 * @since 2021-02-01 12:03
 **/
@Service
public class EmailEventListener implements ApplicationListener<UserRegisterEvent>, Ordered {

    @Override
    public void onApplicationEvent(UserRegisterEvent userRegisterEvent) {
        System.out.println("执行发送email事件, userId = " + userRegisterEvent.getUserId());
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
