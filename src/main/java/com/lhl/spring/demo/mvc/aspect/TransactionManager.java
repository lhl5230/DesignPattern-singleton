package com.lhl.spring.demo.mvc.aspect;

/**
 * Created by hongliang.liu on 2018/5/20.
 */
public class TransactionManager {
    public void begin() {
        System.out.println("begin transaction...");
    }

    public void rollback() {
        System.out.println("roll back...");
    }

    public void commit() {
        System.out.println("commit...");
    }
}
