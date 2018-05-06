package com.lhl.spring.framework.beans.factory.config;

import com.sun.istack.internal.Nullable;

/**
 * 用来存储配置文件中的信息
 * 相当于保存在内存中的配置
 * Created by hongliang.liu on 2018/5/6.
 */

public class MyBeanDefinition {
    private String beanClassName;
    private String factoryBeanName;
    private boolean lazyInit;

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }


    public String getBeanClassName() {
        return beanClassName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }


    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
}
