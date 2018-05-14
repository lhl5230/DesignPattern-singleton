package com.lhl.spring.framework.beans;

import com.lhl.spring.framework.aop.MyAopProxy;
import com.lhl.spring.framework.beans.factory.MyFactoryBean;
import com.lhl.spring.framework.beans.factory.config.MyBeanPostProcessor;

/**
 * Created by hongliang.liu on 2018/5/7.
 */
public class MyBeanWrapper implements MyFactoryBean {

    private MyBeanPostProcessor beanPostProcessor;
    //包装了的对象,动态代理生成
    private Object wrapperInstance;
    //原生对象，通过反射new出来的，要包装起来存下来
    private Object originalInstance;

    private MyAopProxy aopProxy = new MyAopProxy();

    public MyBeanWrapper(Object instance) {
        //用动态代理生成对象
        this.wrapperInstance = aopProxy.getProxy(instance);
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
