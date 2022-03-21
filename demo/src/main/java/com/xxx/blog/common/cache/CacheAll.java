package com.xxx.blog.common.cache;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheAll {

    //缓存标识
    String name() default "";

}