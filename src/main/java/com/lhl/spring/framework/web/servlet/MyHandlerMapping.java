package com.lhl.spring.framework.web.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by hongliang.liu on 2018/5/11.
 */
public class MyHandlerMapping {
    private Pattern urlPattern; //URL正则匹配
    private Method method; //URL对应的方法
    private Object controller;

    public MyHandlerMapping(Pattern urlPattern, Object controller, Method method) {
        this.urlPattern = urlPattern;
        this.method = method;
        this.controller = controller;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Pattern getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(Pattern urlPattern) {
        this.urlPattern = urlPattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
