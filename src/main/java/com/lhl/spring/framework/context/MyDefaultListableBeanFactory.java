package com.lhl.spring.framework.context;

import com.lhl.spring.framework.beans.factory.config.MyBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hongliang.liu on 2018/5/14.
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {
    //保存配置信息
    protected Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    protected void onRefresh() throws Exception {
        super.onRefresh();
    }

    @Override
    protected void refreshBeanFactory() {

    }
}
