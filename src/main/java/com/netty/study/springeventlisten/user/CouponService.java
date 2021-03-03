package com.netty.study.springeventlisten.user;

import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

/**
 * @author WangChen
 * @since 2021-02-01 12:10
 **/
@Service
public class CouponService implements ApplicationListener<UserRegisterEvent> , Ordered {

    @Override
    public void onApplicationEvent(UserRegisterEvent userRegisterEvent) {
        System.out.println("[addCoupon][给用户" +  userRegisterEvent.getUserId() + "发放优惠劵]");
    }

    @Override
    public int getOrder() {
        return 101;
    }
}
