package com.lhl.spring.framework.web.servlet;

import com.lhl.spring.annotation.MyAutowried;
import com.lhl.spring.annotation.MyController;
import com.lhl.spring.annotation.MyService;
import com.lhl.spring.demo.mvc.action.MyAction;
import com.lhl.spring.framework.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hongliang.liu on 2018/5/5.
 */
public class DispatchServlet extends HttpServlet {
    private Properties contextConfig = new Properties(); //读取配置文件
    private Map<String, Object> beanMap = new ConcurrentHashMap<String, Object>(); //IOC容器
    private List<String> classNames = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("=======doPost========");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化
        MyApplicationContext context = new MyApplicationContext(config.getInitParameter("contextConfigLocation"));
       /* //定位,读取的web.xml下的contextConfigLocation配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //加载
        doScanner(contextConfig.getProperty("scanPackage"));
        //注册
        doRegistry();
        //自动依赖注入，可加参数控制，lazy-init
        doAutowired();

        //springMvc,有HandlerMapping
        initHandlerMapping();

        MyAction action = (MyAction) beanMap.get("myAction");
        action.query(null,null,"lhl");*/

    }

    /**
     * 去读配置文件
     *
     * @param location
     */
    private void doLoadConfig(String location) {
        //classpath:application.properties，把配置的值截取掉多余的字符
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));
        try {
            contextConfig.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    /**
     * 扫描配置文件中的包下的类,保存到容器（List）中
     */
    private void doScanner(String packName) {
        URL url = this.getClass().getClassLoader().getResource(packName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File f : classDir.listFiles()) {
            if (f.isDirectory()) {
                doScanner(packName + "." + f.getName());
            } else {
                classNames.add(packName + "." + f.getName().replace(".class", ""));
            }
        }
    }


    /**
     * 注册， 实例化扫描出来的对象
     */
    private void doRegistry() {
        if (classNames.isEmpty()) return;
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                //Spring中用多个子方法来处理
                if (clazz.isAnnotationPresent(MyController.class)) {
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    //在Spring中在这个阶段不是不会直接put instance，这里put的是BeanDefinition
                    beanMap.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService service = clazz.getAnnotation(MyService.class);
                    //自定义的service名称，如果没有，就用类名首字母小写
                    String beanName = service.value();
                    if ("".equals(beanName.trim()))
                        beanName = lowerFirstCase(clazz.getSimpleName());

                    Object instance = clazz.newInstance();

                    beanMap.put(beanName, instance);

                    //获取接口,把service对应的接口名称也保存起来，存对应的实现类实例
                    // 因为controller下设置的属性一般都是接口(如MyAction下定义的IDemoService属性)，保存起来方便自动注入的时候能注入成功
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> c : interfaces) {
                        beanMap.put(c.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动注入属性
     */
    private void doAutowired() {
        if (beanMap.isEmpty()) return;
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object instance = entry.getValue();
            //遍历属性，看是否有加MyAutowried注释，加了就从加载了的类中查找对应的实例赋值
            System.out.println(instance);

            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(MyAutowried.class))
                    continue;
                MyAutowried autowried = field.getAnnotation(MyAutowried.class);
                String beanName = autowried.value().trim();
                if ("".equals(beanName))
                    beanName = field.getType().getName();

                field.setAccessible(true);
                try {
                    //给对象属性赋值
                    field.set(instance,beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private void initHandlerMapping() {
    }


    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
