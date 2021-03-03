package com.netty.study.springeventlisten.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @author WangChen
 * @since 2021-02-01 11:59
 **/
@Component
public class UserService implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    public void registerUser(){
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, "123"));
    }
}
