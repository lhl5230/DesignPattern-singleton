package com.lhl.spring.framework.web.servlet;

import com.lhl.spring.annotation.MyController;
import com.lhl.spring.annotation.MyRequestMapping;
import com.lhl.spring.annotation.MyRequestParam;
import com.lhl.spring.framework.aop.MyAopProxyUtils;
import com.lhl.spring.framework.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hongliang.liu on 2018/5/5.
 */
public class MyDispatchServlet extends HttpServlet {
    private List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();
    private Map<MyHandlerMapping, MyHandlerAdapter> handlerAdapters = new HashMap<MyHandlerMapping, MyHandlerAdapter>();
    private List<MyViewResolver> viewResolvers = new ArrayList<MyViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化
        MyApplicationContext context = new MyApplicationContext(config.getInitParameter("contextConfigLocation"));

        //spring mvc 流程
        initStrategies(context);

    }

    /**
     * 处理请求
     *
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //获取handlermapping
        MyHandlerMapping handler = getHandler(req);
        if (handler == null)
            return;


        MyHandlerAdapter ha = handlerAdapters.get(handler);
        if (ha == null)
            return;

        //方法参数名和位置对应关系， 放到adapter里面去处理
        MyModelAndView mv = ha.handler(req, resp, handler);
        //这一步才是真的输出
        processDispatchResult(resp, mv);
    }

    private void processDispatchResult(HttpServletResponse resp, MyModelAndView mv) throws Exception {
        for(MyViewResolver viewResolver : viewResolvers) {
            //根据返回值，解析模板（页面）里的参数
            String result = viewResolver.resolver(mv);

//            FileInputStream in =  new FileInputStream(viewResolver.getTemplateFile());
//            BufferedReader reader = new BufferedReader(new FileReader(viewResolver.getTemplateFile()));
//            StringBuffer sb = new StringBuffer();
//            String line = null;
//            while((line = reader.readLine()) != null)
//                sb.append(line);

            resp.getWriter().write(result);
        }

    }

    /**
     * 根据URL获取handler
     *
     * @param req
     * @return
     */
    private MyHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) return null;

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/"); //去掉项目名称，和多余的空格
        for (MyHandlerMapping handlerMapping : handlerMappings) {
            Pattern pattern = handlerMapping.getUrlPattern();
            Matcher matcher = pattern.matcher(url);
            if (!matcher.matches()) continue;
            return handlerMapping;
        }
        return null;
    }

    /**
     * 初始化9种策略
     *
     * @param context
     */
    private void initStrategies(MyApplicationContext context) {
//        initMultipartResolver(context);//文件上传解析，如果请求类型是multipart将通过MultipartResolver进行文件上传解析
//        initLocaleResolver(context);//本地化解析
//        initThemeResolver(context);//主题解析

        /** 我们自己会实现 */
        //GPHandlerMapping 用来保存Controller中配置的RequestMapping和Method的一个对应关系
        initHandlerMappings(context);//通过HandlerMapping，将请求映射到处理器
        /** 我们自己会实现 */
        //HandlerAdapters 用来动态匹配Method参数，包括类转换，动态赋值
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配

//        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给HandlerExceptionResolver来解析
//        initRequestToViewNameTranslator(context);//直接解析请求到视图名

        /** 我们自己会实现 */
        //通过ViewResolvers实现动态模板的解析
        //自己解析一套模板语言
        initViewResolvers(context);//通过viewResolver解析逻辑视图到具体视图实现

//        initFlashMapManager(context);//flash映射管理器
    }

    /**
     * 解析视图 ,页面名字和模板文件的关联
     *
     * @param context
     */
    private void initViewResolvers(MyApplicationContext context) {
        String templateRoot = context.getConfigProperty("templateRoot");
        URL url = this.getClass().getClassLoader().getResource(templateRoot);
        if (url != null) {
            String templateRootPath = url.getFile();
            File templateRootDir = new File(templateRootPath);
            for (File templateFile : templateRootDir.listFiles()) {
                viewResolvers.add(new MyViewResolver(templateFile.getName(), templateFile));
            }
        }

    }

    /**
     * 方法参数解析
     *
     * @param context
     */
    private void initHandlerAdapters(MyApplicationContext context) {
        for (MyHandlerMapping handlerMapping : handlerMappings) {
            //每一个方法有一个参数列表，那么这里保存的是形参列表
            Map<String, Integer> paramMapping = new HashMap<String, Integer>();

            Method method = handlerMapping.getMethod();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                //参数名参考资料
                //https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html
                //https://www.liaoxuefeng.com/article/00141999088629621039ee8c4614579bfedb78a5030bce3000
                String paramName = parameter.getName(); //编译时需要加上javac -parameters来打开保留参数名选项，不然获取到的名称为argN表示，N为参数index
                Annotation[] pAnnotations = parameter.getAnnotations();
                boolean isParamAnno = false;
                for (Annotation a : pAnnotations) {
                    if (a instanceof MyRequestParam) { //如果有注解参数的，获取注解参数名称
                        isParamAnno = true;
                        paramName = ((MyRequestParam) a).value().trim();
                        if ("".equals(paramName)) //注解没自定义名称，则用参数名称
                            paramName = parameter.getName();
                        paramMapping.put(paramName, i);
                    }
                }
                if (!isParamAnno) {
                    paramMapping.put(parameter.getType().getName(), i); //没注解的用typename作为名称
                }
            }

            handlerAdapters.put(handlerMapping, new MyHandlerAdapter(paramMapping));
//            //另一种方法，先遍历加了注解的参数， 再遍历没注解的参数，会有重复方法，直接用上面的方法运行
//            Annotation[][] annotations = method.getParameterAnnotations(); //方法参数可以有多个注解，所以返回二位数组
//            for (int i = 0; i < annotations.length; i++) {
//                for (Annotation a : annotations[i]) {
//                    if (a instanceof MyRequestParam) {
//                        String paramName = ((MyRequestParam) a).value();
//                        if (!"".equals(paramName.trim())) {
//                            paramMapping.put(paramName, i);
//                        } else {//注解没自定义名称，则用参数名称
//                            Parameter parameter = parameters[i];
//                            paramMapping.put(parameter.getName(), i);
//                        }
//                    }
//                }
//            }
//
//            System.out.println(paramMapping);
        }
    }

    /**
     * controller和方法的对应关系
     *
     * @param context
     */
    private void initHandlerMappings(MyApplicationContext context) {
        //容器中获取所有的实例
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                //返回的是代理对象
                Object proxyController = context.getBean(beanName);
                //要从代理对象中,获取到原始对象
                Object controller = MyAopProxyUtils.getTargetObject(proxyController);

                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(MyController.class)) {
                    continue;
                }

                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                String baseUrl = requestMapping.value(); //方法上基础地址

                //获取类的方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    //方法上是否加了requestmapping
                    if (!method.isAnnotationPresent(MyRequestMapping.class))
                        continue;
                    MyRequestMapping methodMapping = method.getAnnotation(MyRequestMapping.class);
                    String requestUrl = methodMapping.value()
                            .replaceAll("\\*", ".*") //有通配符*的，改成正则 .*，表示任意字符0次或多次
                            .replaceAll("/+", "/"); //访问方法url地址

                    String realUrl = baseUrl + requestUrl; //真的请求地址
                    Pattern pattern = Pattern.compile(realUrl);

                    handlerMappings.add(new MyHandlerMapping(pattern, controller, method));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

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
