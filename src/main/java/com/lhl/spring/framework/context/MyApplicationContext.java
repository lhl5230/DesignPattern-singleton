package com.lhl.spring.framework.context;

import com.lhl.spring.annotation.MyAutowried;
import com.lhl.spring.annotation.MyController;
import com.lhl.spring.annotation.MyService;
import com.lhl.spring.framework.aop.MyAopConfig;
import com.lhl.spring.framework.beans.MyBeanWrapper;
import com.lhl.spring.framework.beans.factory.MyBeanDefinitionReader;
import com.lhl.spring.framework.beans.factory.MyBeanFactory;
import com.lhl.spring.framework.beans.factory.config.MyBeanDefinition;
import com.lhl.spring.framework.beans.factory.config.MyBeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hongliang.liu on 2018/5/6.
 */
//模仿ClassPathXmlApplicationContext
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {
    private String[] configLocations;

    private MyBeanDefinitionReader reader;

    //用来保证注册式单例
    private Map<String, Object> beanCacheMap = new HashMap<String, Object>();
    //用来存储所有被代理了的类
    private Map<String, MyBeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

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
        doAutowired();
//        MyAction action = (MyAction) this.getBean("myAction");
//        action.query(null, null, "lhl");
//        System.out.println();
    }

    //自动注入
    private void doAutowired() {
        for (Map.Entry<String, MyBeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                Object obj = getBean(beanName);
            }
        }

    }

    public void populateBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();
        //只对对应的注解类注解
        if (!(clazz.isAnnotationPresent(MyController.class) ||
                clazz.isAnnotationPresent(MyService.class)))
            return;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(MyAutowried.class))
                continue;

            MyAutowried autowried = field.getAnnotation(MyAutowried.class);
            String autowiredBeanName = autowried.value().trim(); //自动注入对象名称
            if ("".equals(autowiredBeanName))
                autowiredBeanName = field.getType().getName();
            field.setAccessible(true);
            try {
                if (beanWrapperMap.get(autowiredBeanName) == null)
                    getBean(autowiredBeanName);
                field.set(instance, beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
    public Object getBean(String beanName) {
        MyBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        String className = beanDefinition.getBeanClassName();
        try {
            //生成通知事件
            MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();

            Object instance = instantiationBean(beanDefinition);
            if (instance == null)
                return null;
            //实例初始化之前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

            MyBeanWrapper wrapper = new MyBeanWrapper(instance);
            wrapper.setMyAopConfig(instantiationAopConfig(beanDefinition));

            this.beanWrapperMap.put(beanName, wrapper);
            //实例初始化后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);

            populateBean(beanName, instance);
            return beanWrapperMap.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化Aop配置,切入点切面等
     *
     * @param beanDefinition
     * @return
     */
    private MyAopConfig instantiationAopConfig(MyBeanDefinition beanDefinition) throws Exception {
        MyAopConfig config = new MyAopConfig();
        String expression = reader.getConfigs()[0].getProperty("pointCut");
        String[] before = reader.getConfigs()[0].getProperty("aspectBefore").split("\\s");
        String[] after = reader.getConfigs()[0].getProperty("aspectAfter").split("\\s");
        String className = beanDefinition.getBeanClassName();
        Class<?> clazz = Class.forName(className);

        Pattern pattern = Pattern.compile(expression);

        Class aspectClass = Class.forName(before[0]);
        for (Method m : clazz.getMethods()) {
            Matcher matcher = pattern.matcher(m.toString()); //方法名是否匹配规则
            if (matcher.matches()) {
                //满足切面规则的类,添加到AOP配置中
                config.put(m, aspectClass.newInstance(), new Method[]{aspectClass.getMethod(before[1]), aspectClass.getMethod(after[1])});
            }
        }
        return config;
    }

    //返回一个实例Bean,暂时没保证线程安全
    private Object instantiationBean(MyBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if (beanCacheMap.containsKey(className)) {
                instance = beanCacheMap.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                beanCacheMap.put(className, instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public String getConfigProperty(String key) {
        Properties[] props = reader.getConfigs();
        for (Properties p : props) {
            if (p.containsKey(key))
                return p.getProperty(key);
        }
        return null;
    }
}
