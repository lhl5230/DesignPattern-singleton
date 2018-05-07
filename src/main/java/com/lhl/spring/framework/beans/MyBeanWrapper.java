package com.lhl.spring.framework.beans;

import com.lhl.spring.framework.beans.factory.MyFactoryBean;
import com.lhl.spring.framework.beans.factory.config.MyBeanPostProcessor;

/**
 * Created by hongliang.liu on 2018/5/7.
 */
public class MyBeanWrapper implements MyFactoryBean {

    private MyBeanPostProcessor beanPostProcessor;
    private Object wrapperInstance;//包装了的对象
    private Object originalInstance; //原生对象，通过反射new出来的，要包装起来存下来

    public MyBeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.originalInstance = instance;
    }

    public Object getWrappedInstance() {
        return this.wrapperInstance;
    }

    //返回代理后的Class 可能是$Proxy0
    public Class<?> getWrappedClass() {
        return this.wrapperInstance.getClass();
    }
}
