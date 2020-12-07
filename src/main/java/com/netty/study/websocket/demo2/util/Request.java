package com.netty.study.websocket.demo2.util;

import com.google.gson.Gson;
import org.apache.logging.log4j.util.Strings;

/**
 * @author WangChen
 * @since 2020-12-07 17:04
 **/
public class Request {

    private static Gson gson = new Gson();

    private String requestId;
    private int serviceId;
    private String name;
    private String message;

    public String getRequestId() {
        return requestId;
    }
    public Request setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
    public int getServiceId() {
        return serviceId;
    }
    public Request setServiceId(int serviceId) {
        this.serviceId = serviceId;
        return this;
    }
    public String getName() {
        return name;
    }
    public Request setName(String name) {
        this.name = name;
        return this;
    }
    public String getMessage() {
        return message;
    }
    public Request setMessage(String message) {
        this.message = message;
        return this;
    }

    public static Request create(String json) {
        if (!Strings.isNotBlank(json)) {
            return gson.fromJson(json, Request.class);
        }
        return null;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
