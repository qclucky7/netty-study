package com.netty.study.springeventlisten.activity;

import org.springframework.context.ApplicationListener;

/**
 * @author WangChen
 * @since 2021-02-02 09:04
 **/
public class RewardEventListener implements ApplicationListener<ActivityEvent> {

    @Override
    public void onApplicationEvent(ActivityEvent activityEvent) {
        System.out.println("触发[" + activityEvent.getCode() + "] 奖励！");
    }
}
