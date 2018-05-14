package com.lhl.spring.framework.beans.factory;

import com.lhl.spring.framework.beans.factory.config.MyBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 对配置文件进行查找，读取，解析
 * 读取BeanDefinition类
 * Created by hongliang.liu on 2018/5/6.
 */
public class MyBeanDefinitionReader {
    private Properties[] configs;
    private List<String> registyBeanClassNames = new ArrayList<>(); //需要注册的bean的名称

    public MyBeanDefinitionReader(String... locations) {
        configs = new Properties[locations.length];
        for (int i = 0; i < locations.length; i++) {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(locations[i].replace("classpath:", ""));
            try {
                configs[i] = new Properties();
                configs[i].load(in);
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

        for (Properties p : configs)
            doScanner(p.getProperty("scanPackage"));

    }

    public List<String> loadBeanDefinitions() {
        return registyBeanClassNames;
    }

    //获取bean的一个包装类
    public MyBeanDefinition registerBeanDefinition(String className) {
        if (registyBeanClassNames.contains(className)) {
            MyBeanDefinition beanDefinition = new MyBeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".")+1)));
            return beanDefinition;
        }
        return null;

    }

//    public BeanDefinitionRegistry getRegistry(){
//
//    }

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
                registyBeanClassNames.add(packName + "." + f.getName().replace(".class", ""));
            }
        }
    }

    public Properties[] getConfigs() {
        return configs;
    }

    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
