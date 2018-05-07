package com.lhl.spring.framework.beans.factory.config;

/**
 * 用于做事件监听
 * Created by hongliang.liu on 2018/5/7.
 */
public class MyBeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)  {
        return bean;
    }
}
