package com.netty.study.springstrategymodel;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author WangChen
 * @since 2021-02-01 13:49
 **/
@Component
public class PayStrategy implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private static ConcurrentMap<SourceType, EventStrategy> payStrategyMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventStrategy> beansOfType = applicationContext.getBeansOfType(EventStrategy.class);
        beansOfType.forEach((name, strategy) -> {
            payStrategyMap.put(strategy.getSource(), strategy);
        });
        System.out.println("payStrategyMap = " + payStrategyMap.toString());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void on(SourceType sourceType){
        payStrategyMap.get(sourceType).on();
    }
}
