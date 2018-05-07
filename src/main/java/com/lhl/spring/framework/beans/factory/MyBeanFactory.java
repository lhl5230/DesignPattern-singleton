package com.lhl.spring.framework.beans.factory;

/**
 * Created by hongliang.liu on 2018/5/6.
 */
public interface MyBeanFactory {
    /**
     * 根据beanName从IOC容器获取bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);
}
