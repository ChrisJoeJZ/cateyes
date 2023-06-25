package cateye.chris.aop.annotation;

import java.lang.annotation.*;

//注解类
@Documented
@Retention(RetentionPolicy.RUNTIME)   //运行时注解
@Target(ElementType.METHOD)     //该注解的作用范围-方法
public @interface  RedisCache {
    //redis缓存的有效期（单位：秒）
    long duration() default -1;
}
