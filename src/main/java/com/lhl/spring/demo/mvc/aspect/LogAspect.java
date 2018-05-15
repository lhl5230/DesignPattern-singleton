package com.lhl.spring.demo.mvc.aspect;

/**
 * Created by hongliang.liu on 2018/5/14.
 */
public class LogAspect {
    //在调用一个方法之前，执行这个方法
    public void before() {
        System.out.println("method before log " );
    }

    //在调用一个方法之后，执行这个方法
    public void after() {
        System.out.println("method after log");
    }
}
