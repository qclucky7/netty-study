package com.netty.study.springstrategymodel;

/**
 * @author WangChen
 * @since 2021-02-01 13:41
 **/
public interface EventStrategy extends Strategy{

    /**
     * 得到执行策略类型
     * @return SourceType
     */
    SourceType getSource();

}
