package cateye.chris.aop.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//作用于视图方法的Method
//凡是标记了该注解的方法，要执行authorization判断（凡是需要验证登陆状态的方法都要加上该注解）
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TokenValid {
}
