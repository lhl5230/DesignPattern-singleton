package com.lhl.spring.framework.context;

import com.lhl.spring.framework.beans.factory.MyBeanDefinitionReader;
import com.lhl.spring.framework.beans.factory.MyBeanFactory;
import com.lhl.spring.framework.beans.factory.config.MyBeanDefinition;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hongliang.liu on 2018/5/6.
 */
//模仿ClassPathXmlApplicationContext
public class MyApplicationContext implements MyBeanFactory {
    private String[] configLocations;

    private MyBeanDefinitionReader reader;
    private Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public MyApplicationContext(String... locations) {
        this.configLocations = locations;
        refresh();
    }


    public void refresh() {
        //定位
        this.reader = new MyBeanDefinitionReader(configLocations);
        //加载
        List<String> beanDefinitions = reader.loadBeanDefinitions();

        //注册
        doRegistry(beanDefinitions);
        //依赖注入

    }

    //真正将beanDefinition注册到map中
    private void doRegistry(List<String> beanDefinitions) {
        try {
            for (String className : beanDefinitions) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) continue;

                MyBeanDefinition beanDefinition = reader.registerBeanDefinition(className);
                if (beanDefinition != null) {
                    beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                }
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    beanDefinitionMap.put(i.getName(), beanDefinition);
                }
                //beanName有三种情况
                //1.默认是类名称首字母小写
                //2.自定义名称
                //3.接口注入

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //通过去读BeanDefinition中的信息，通过反射创建一个实例并返回
    //Spring不会把最原始的对象放出去，用一个BeanWrapper进行一次包装
    @Override
    public Object getBean(String name) {
        return null;
    }
}
