package com.lhl.spring.framework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by hongliang.liu on 2018/5/11.
 */
public class MyHandlerAdapter {
    private Map<String, Integer> paramMapping;

    public MyHandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    public Map<String, Integer> getParamMapping() {
        return paramMapping;
    }

    public void setParamMapping(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    /**
     * 单独的适配器处理请求参数赋值给对应方法，
     *
     * @param req
     * @param resp
     * @param handler
     */
    public MyModelAndView handler(HttpServletRequest req, HttpServletResponse resp, MyHandlerMapping handler) throws Exception {
        //请求地址中传入的参数
        Map<String, String[]> reqParamterMap = req.getParameterMap();
        Method method = handler.getMethod();
//        Parameter[] parameters = method.getParameters();

        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] paramValues = new Object[parameterTypes.length];
        for (String key : reqParamterMap.keySet()) {
            if (paramMapping.get(key) == null) continue;
            //传进来的参数的值
            String[] values = reqParamterMap.get(key);
            String value = Arrays.toString(values).replaceAll("\\[|\\]", "").replaceAll("\\s", "");

            Integer index = paramMapping.get(key);

            //因为页面上传过来的值都是String类型的，而在方法中定义的类型是千变万化的
            //要针对我们传过来的参数进行类型转换
            //获取到对应参数索引的值
            paramValues[index] = caseValueToString(value, parameterTypes[index]);

        }
        //方法中req ,resp 没赋值
        if (paramMapping.get(HttpServletRequest.class.getName()) != null) {
            int index = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        if (paramMapping.get(HttpServletResponse.class.getName()) != null) {
            int index = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }


        Object result = method.invoke(handler.getController(), paramValues);
        if (result == null)
            return null;
        boolean isModelAndView = method.getReturnType() == MyModelAndView.class;
        if (isModelAndView) {
            return (MyModelAndView) result;
        } else {
            return null;
        }

    }

    /**
     * 根据类型转换传入的值
     *
     * @param value
     * @param parameterType
     * @return
     */
    private Object caseValueToString(String value, Class<?> parameterType) {
        if (parameterType == String.class)
            return value;
        else if (parameterType == Integer.class)
            return Integer.valueOf(value);
        else if (parameterType == int.class)
            return Integer.valueOf(value);
        else if (parameterType == Double.class)
            return Double.parseDouble(value);
        return null;

    }
}
