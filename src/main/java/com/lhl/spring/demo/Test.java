package com.lhl.spring.demo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by hongliang.liu on 2018/5/11.
 */
public class Test {
    private void query(String name){

    }
    public static void main(String[] args) {
        for (Method m : Test.class.getMethods()) {
            System.out.println("----------------------------------------");
            System.out.println("   method: " + m.getName());
            System.out.println("   return: " + m.getReturnType().getName());
            for (Parameter p : m.getParameters()) {
                System.out.println("parameter: " + p.getType().getName() + ", " + p.getName());
            }
        }


    }
}
