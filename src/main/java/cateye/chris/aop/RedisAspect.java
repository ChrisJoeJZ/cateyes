package cateye.chris.aop;

import cateye.chris.aop.annotation.RedisCache;
import cateye.chris.utils.RedisUtils;
import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

//切面类，实现功能：对于已经增加了@redisCache注解的方法应用redis策略
@Aspect    //声明这是一个切面类
@Component   //托管给spring框架
public class RedisAspect {

    //使用redis的下层对象
    @Resource
    private RedisUtils redisUtils;

    //  Pointcut切入点，描述execution织入表达式
    @Pointcut("execution(* cateye.chris.service.impl.*.*(..))")
    public void pointCut(){}

    // 环绕增强方法 = 前置增强 + 后置增强 + 返回值增强 + 异常增强
    @Around("pointCut()")
    public Object around( ProceedingJoinPoint joinPoint ) throws Throwable {

        //在这里要加前置增强的代码-从redis读取缓存
        //要通过反射的机制获取被代理的service方法的注解列表，判断她是否有@RedisCache注解
        Class cls = joinPoint.getSignature().getDeclaringType();   //获取被代理目标service方法类型
        Method method = null;                                      //被代理的目标service方法
        for( Method m : cls.getDeclaredMethods() ){
            if( m.getName().equals( joinPoint.getSignature().getName() ) ){
                method = m;
            }
        }
        //getDeclaredAnnotation获取service方法上的注解信息（获取不到继承来的注解）
        RedisCache annotation = method.getDeclaredAnnotation(RedisCache.class);
        if( annotation == null ){
            System.out.println("===> 无缓存策略，直接从数据库查询数据");
            return joinPoint.proceed();
        }else {
            System.out.println("===>有缓存策略，从redis中获取数据");
        }

        //需要根据被代理的service方法+形参构建一个redisKey
        //key命名规则：包含类名信息+方法名+参数值列表 类名信息：cls.getName()  代理的方法名 method.getName（）
        Object[] params = joinPoint.getArgs();   //获取参数值列表
        Map<String,Object> keyMap = new HashMap<>();
        keyMap.put("className",cls.getName());
        keyMap.put("methodName",method.getName());
        keyMap.put("params",params);
        String redisKey = JSON.toJSONString(keyMap);
        //尝试从redis中取“indexPage”的数据 (用aop注解代替)
        Object redisData = redisUtils.get(redisKey);
        //真正取到数据，直接返回或取到了一个”null“的字符串也要返回，预防缓存穿透
        if (redisData != null && redisData.equals("null")){
            //取到了为null的字符串
            redisData = null;
            System.out.println("===> Redis缓存中，直接返回缓存数据");
            return redisData;
        }else if (redisData != null){
            //真正取到了数据
            System.out.println("===> Redis缓存中，直接返回预防缓存穿透的数据");
            return redisData;
        }

        Object data = joinPoint.proceed();    //该业务方法的核心逻辑被调用，返回一个object类型的返回值

        //在这里要加上后置增强的代码-要往redis中写数据
        System.out.println("准备往redis写入数据");
        //对于数据库中查询到的数据存放到redis中（需要预防redis缓存雪崩对于mysql的压力过大，需要对duration设置一个随即因子）
        if (data != null){
            long duration = annotation.duration();
            duration = duration + (long) (Math.random()*duration/10); //(在已有设置duration参数的基础上，从100%duration-110%duration浮动)
            redisUtils.set(redisKey,data,duration);    //缓存事件用改从redisCache的duration中获取
        }else {
            //从数据库中取到的data数据为null==>预防缓存穿透，需要往redis中存一个”null“，事件为五秒
            redisUtils.set(redisKey,"null",5);
        }

        return data;
    }
}
