package com.lhl.spring.annotation;

import java.lang.annotation.*;

/**
 * Created by hongliang.liu on 2018/5/6.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAutowried {
    String value() default  "";
}
