package com.netty.study.springeventlisten.user;

import org.springframework.context.ApplicationEvent;

/**
 * @author WangChen
 * @since 2021-02-01 11:57
 **/
public class UserRegisterEvent extends ApplicationEvent {

    private String userId;

    public UserRegisterEvent(Object source, String userId) {
        super(source);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
