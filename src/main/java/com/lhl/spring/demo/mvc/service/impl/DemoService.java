package com.lhl.spring.demo.mvc.service.impl;

import com.lhl.spring.annotation.MyService;
import com.lhl.spring.demo.mvc.service.IDemoService;

/**
 * Created by hongliang.liu on 2018/5/6.
 */
@MyService
public class DemoService implements IDemoService {
    @Override
    public String getName(String name) throws Exception{
        String result = "My name is " + name;
        System.out.println(result);
        throw new Exception("模拟异常");
//        return result;
    }
}
