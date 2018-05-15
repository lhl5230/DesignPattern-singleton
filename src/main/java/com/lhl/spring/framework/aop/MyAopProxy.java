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

    private Object target; //原始对象

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
        //method对象为接口类方法，如：public abstract java.lang.String com.lhl.spring.demo.mvc.service.IDemoService.getName(java.lang.String)
        Method m = target.getClass().getMethod(method.getName(),method.getParameterTypes());
        //但config里面保存的是切点方法，为具体的实现类，如：public java.lang.String com.lhl.spring.demo.mvc.service.impl.DemoService.getName(java.lang.String)
        boolean contains = config.contains(m);
        MyAopConfig.MyAspect aspect = null;
        if(contains) {
            aspect = config.get(m);
            aspect.getPoints()[0].invoke(aspect.getAspect());
        }
        Object obj = m.invoke(this.target,args);

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
