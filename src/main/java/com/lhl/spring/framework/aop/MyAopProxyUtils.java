package com.lhl.spring.framework.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * 代理对象工具类
 * Created by hongliang.liu on 2018/5/15.
 */
public class MyAopProxyUtils {
    public static Object getTargetObject(Object proxyObj) throws Exception {
        if (!isAopProxy(proxyObj)) return proxyObj;
        return getProxyTargetObject(proxyObj);
    }

    private static boolean isAopProxy(Object object) {
        return Proxy.isProxyClass(object.getClass());
    }

    /**
     * 从代理对象中获取原始对象
     *
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getProxyTargetObject(Object proxy) throws Exception {
        Field field = proxy.getClass().getSuperclass().getDeclaredField("h");
        field.setAccessible(true);
        MyAopProxy aopProxy = (MyAopProxy) field.get(proxy);
        Field targetField = aopProxy.getClass().getDeclaredField("target");
        targetField.setAccessible(true);
        return targetField.get(aopProxy);
    }
}
