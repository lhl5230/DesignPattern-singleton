package com.lhl.spring.framework.web.servlet;

import java.util.Map;

/**
 * Created by hongliang.liu on 2018/5/12.
 */
public class MyModelAndView {
    private String viewName;//视图名称
    private Map<String, ?> model; //结果集

    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
