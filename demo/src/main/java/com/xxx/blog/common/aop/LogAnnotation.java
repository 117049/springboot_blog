package com.xxx.blog.common.aop;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD}) // 代表可以放在方法和类上面
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    String model() default "";
    String operater() default "";
}
