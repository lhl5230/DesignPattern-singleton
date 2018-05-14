package com.lhl.spring.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Aop代理类，默认用JDK动态代理
 * Created by hongliang.liu on 2018/5/14.
 */
public class MyAopProxy implements InvocationHandler {
    private MyAopConfig config;

    private Object target; //生成的代理对象

    /**
     * @param original 原始对象
     * @return
     */
    public Object getProxy(Object original) {
        this.target = original;
        Class<?> clazz = original.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean contains = config.contains(method);
        MyAopConfig.MyAspect aspect = null;
        if(contains) {
            aspect = config.get(method);
            aspect.getPoints()[0].invoke(aspect.getAspect());
        }
        Object obj = method.invoke(this.target,args);

        if(contains && aspect != null) {
            aspect.getPoints()[1].invoke(aspect.getAspect());
        }
        //返回最原始的值
        return obj;
    }

    public void setConfig(MyAopConfig config) {
        this.config = config;
    }
}
