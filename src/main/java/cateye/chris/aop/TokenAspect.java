package cateye.chris.aop;

import cateye.chris.aop.annotation.RedisCache;
import cateye.chris.aop.annotation.TokenValid;
import cateye.chris.bean.po.UserInfo;
import cateye.chris.utils.RedisUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.frameworkset.util.annotations.RequestHeader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

//登陆状态的切面类，作用域controller层所有的业务方法
//职责是判断传入的authorization是否合法
@Component
@Aspect
public class TokenAspect {
    @Resource
    RedisUtils redisUtils;

    //切入点，作用于controller层的所有方法
    @Pointcut("execution(* cateye.chris.controller.*.*(..))")
    public void pointCut(){}

    //增强方法
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{

        System.out.println("开始tokenAspect增强");
        //并不是所有的视图方法都要被代理，收缩一下被代理的方位

        Map<String,Object> responseBody = new HashMap<>();
        Class cls = joinPoint.getSignature().getDeclaringType();   //获取被代理目标service方法类型
        Method method = null;                                      //被代理的目标service方法
        for( Method m : cls.getDeclaredMethods() ){
            if( m.getName().equals( joinPoint.getSignature().getName() ) ){
                method = m;
            }
        }
        //getDeclaredAnnotation获取service方法上的注解信息（获取不到继承来的注解）
        TokenValid annotation = method.getDeclaredAnnotation(TokenValid.class);
        if (annotation == null ){                    //如果没有tokenValid注解则继续执行controller方法  不用在判断token了
            System.out.println("===> 不开启token校验策略，直接执行后续");
            return joinPoint.proceed();
        }
        System.out.println("===>开始tokenAspect增强");

        //把authorization的后续逻辑放进来
        String authorization = " ";
        boolean isHit = false;
        Object[] params = joinPoint.getArgs();
        for (Object param : params) {
            if (param.toString().startsWith("Bearer")){
                authorization = param.toString();                  //可以用这个方法  但是后续如果有其他参数也以Bearer开头可能会出现错误
                System.out.println(authorization);
                isHit = true;
            }
//            System.out.println(param.toString());
        }

//        Annotation[][] annotations = method.getParameterAnnotations();   //获取传入参数的注解标签，
//        for (int i = 0; i < annotations.length; i++) {
//            for (int j = 0; j < annotations[i].length; j++){
//                if (annotations[i][j].annotationType() == RequestHeader.class){      //取到注解为@RequestHeader的参数
//                    isHit = true;
//                    authorization = params[i].toString();
//                }
//            }
//        }

        if (isHit == false){
            System.out.println("Illegal Token");
            responseBody.put("code",401 );
            responseBody.put("message","authorization failed");
            return responseBody;
        }

        //从method中获取authorization数据值


        //先获取到从客户端发送来的token数据
        String[] array = authorization.split(" ");  //首次利用传入的authorization参数，进行数组解析（也可用一个解析用户登录状态的切面类来验证， token切面类的开始）
        String token;
        if (array !=null && array.length>1){
            //得到token的字符串
            token = array[1];  //token的正文数据是以空格分割的 空格之后的为token的数据 所以拿到索引为1的字符串

            //从redis中get用户登录后返回的token
            UserInfo userInfo = (UserInfo) redisUtils.get(token);
            if (userInfo == null){
                //从redis去token，失败（token过期了/redis中根本没有登录的token），返回401
//                System.out.println("Illegal Token");
                responseBody.put("code",401 );
                responseBody.put("message","Illegal Token,authorization failed");
                return responseBody;
            }

            //得到token后要对token进行解密操作 DecodeJWT实例（没有得到实例说明token不合法，返回401错误）
            DecodedJWT decodedJWT = null;
            try {
                decodedJWT = JWT.decode(token);
            }catch (Exception ex){
                ex.printStackTrace();
                if (decodedJWT == null){
                    System.out.println("token不合法");
                    responseBody.put("code",401);
                    responseBody.put("message","Illegal token, authorization failed");
                    return responseBody;
                }
            }
            //判断token是否过期
            //对于DecodeJWT实例判断是否过期（已过期返回401错误）
            if (System.currentTimeMillis() > decodedJWT.getExpiresAt().getTime()){
                //说明该令牌已经过期
                responseBody.put("code",403);
                responseBody.put("message","The token is expired! please login again");
                return responseBody;
            }

        }             //token切面类逻辑结束
        else {
//            System.out.println("token不合法");
            responseBody.put("code",401);
            responseBody.put("message","Illegal token, authorization failed");
            return responseBody;
        }



        return joinPoint.proceed();

    }

}
