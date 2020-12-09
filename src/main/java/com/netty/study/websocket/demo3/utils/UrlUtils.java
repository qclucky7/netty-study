package com.netty.study.websocket.demo3.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WangChen
 * @since 2020-12-08 18:02
 **/
public class UrlUtils {

    public static Map<String,String> getParam(String url){

        String[] urlParts = url.split("\\?");

        //有参数
        if (urlParts.length != 2){
            return Collections.emptyMap();
        }

        String[] params = urlParts[1].split("&");

        Map<String, String> hashMap = new HashMap<>(params.length);
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length != 2){
                continue;
            }
            hashMap.put(keyValue[0], keyValue[1]);
        }

        return hashMap;
    }

}
