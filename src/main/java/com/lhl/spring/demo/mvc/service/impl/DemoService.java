package com.lhl.spring.demo.mvc.service.impl;

import com.lhl.spring.annotation.MyService;
import com.lhl.spring.demo.mvc.service.IDemoService;

/**
 * Created by hongliang.liu on 2018/5/6.
 */
@MyService
public class DemoService implements IDemoService {
    @Override
    public String getName(String name) {
        return "My name is " + name;
    }
}
