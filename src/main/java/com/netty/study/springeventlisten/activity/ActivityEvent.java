package com.netty.study.springeventlisten.activity;

import org.springframework.context.ApplicationEvent;

/**
 * @author WangChen
 * @since 2021-02-02 09:05
 **/
public class ActivityEvent extends ApplicationEvent {

    private String code;

    public ActivityEvent(Object source, String code) {
        super(source);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
