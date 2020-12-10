package com.netty.study.pipeline.monitor;

import com.netty.study.pipeline.pipeline.Handler;

/**
 * @author WangChen
 * @since 2020-12-10 10:54
 **/
@FunctionalInterface
public interface Monitor {

    /**
     * 监听回调
     */
    void callback(Handler handler);
}
