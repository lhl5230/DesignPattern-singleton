package com.lhl.spring.demo.mvc.action;

import com.lhl.spring.annotation.MyAutowried;
import com.lhl.spring.annotation.MyController;
import com.lhl.spring.annotation.MyRequestMapping;
import com.lhl.spring.annotation.MyRequestParam;
import com.lhl.spring.demo.mvc.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hongliang.liu on 2018/5/6.
 */
@MyController
@MyRequestMapping("/demo")
public class MyAction {
    @MyAutowried
    private IDemoService demoService;

    @MyRequestMapping("/query.do")
    public void query(HttpServletRequest req, HttpServletResponse resp, @MyRequestParam("name") String name) {
        String result = demoService.getName(name);
        System.out.println(result);
    }
}
