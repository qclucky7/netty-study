package com.netty.study.springeventlisten.activity;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @author WangChen
 * @since 2021-02-01 11:59
 **/
@Component
public class ActivityService implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    public void notifyReward(){
        applicationEventPublisher.publishEvent(new ActivityEvent(this, "123"));
    }
}
