package com.lhl.spring.framework.context;

/**
 * Created by hongliang.liu on 2018/5/14.
 */
public abstract class MyAbstractApplicationContext {
    //提供给子类复写
    protected void onRefresh() throws Exception {
        // For subclasses: do nothing by default.
    }

    protected abstract void refreshBeanFactory();
}
