package cateye.chris.controller;

import cateye.chris.aop.annotation.TokenValid;
import cateye.chris.bean.po.UserInfo;
import cateye.chris.bean.po.WatchTimes;
import cateye.chris.service.CinemaService;
import cateye.chris.service.FilmService;
import cateye.chris.service.WatchTimesService;
import cateye.chris.utils.RedisUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.jdbc.Null;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

//场次的视图类
@Api(tags = "场次模块")
@RestController
@RequestMapping("/watchtimes")
public class WatchTimesController {
    @Resource
    FilmService filmService;
    @Resource
    CinemaService cinemaService;

    @Resource
    WatchTimesService watchTimesService;
    @Resource
    RedisUtils redisUtils;


    //隐藏需求  编辑一个切面类 （作用：从redis中取token数据，使用JWT解密，判断token是否存在或过期，将该切面类加入到所有需要登录才能访问的视图方法）
    @TokenValid
    @ApiOperation(value = "展现某个场次的信息")
    @GetMapping("/{id}")
    //加了@requestHeader之后，要求客户端或浏览器必须携带需要的参数才能访问该视图方法。如果是希望该参数有无都可，为期加上required = false即可
    public Object getOne(@PathVariable("id") String watchTimesId,@RequestHeader("authorization") String authorization){
        //第一层级
        Map<String,Object> responseBody = new HashMap<>();
        //先获取到从客户端发送来的token数据   使用TokenValid自定义的注解类替代后续注释掉的功能
//        String[] array = authorization.split(" ");  //首次利用传入的authorization参数，进行数组解析（也可用一个解析用户登录状态的切面类来验证， token切面类的开始）
//        String token;
//        if (array !=null && array.length>1){
//            //得到token的字符串
//            token = array[1];  //token的正文数据是以空格分割的 空格之后的为token的数据 所以拿到索引为1的字符串
//
//            //从redis中get用户登录后返回的token
//            UserInfo userInfo = (UserInfo) redisUtils.get(token);
//            if (userInfo == null){
//                //从redis去token，失败（token过期了/redis中根本没有登录的token），返回401
//                System.out.println("Illegal Token");
//                responseBody.put("code",401 );
//                responseBody.put("message","Illegal Token,authorization failed");
//                return responseBody;
//            }
//
//            //得到token后要对token进行解密操作 DecodeJWT实例（没有得到实例说明token不合法，返回401错误）
//            DecodedJWT decodedJWT = null;
//            try {
//               decodedJWT = JWT.decode(token);
//            }catch (Exception ex){
//                ex.printStackTrace();
//                if (decodedJWT == null){
//                    System.out.println("token不合法");
//                    responseBody.put("code",401);
//                    responseBody.put("message","Illegal token, authorization failed");
//                    return responseBody;
//                }
//            }
//            //判断token是否过期
//            //对于DecodeJWT实例判断是否过期（已过期返回401错误）
//            if (System.currentTimeMillis() > decodedJWT.getExpiresAt().getTime()){
//                //说明该令牌已经过期
//                responseBody.put("code",403);
//                responseBody.put("message","The token is expired! please login again");
//                return responseBody;
//            }
//
//        }             //token切面类逻辑结束

        //完成对于token的验证 可正常访问后续信息
        responseBody.put("code",200);
        responseBody.put("message","success");
        //第二层级返回数据（watchTimes，film，cinema）
        Map<String,Object> data =new HashMap<>();
        WatchTimes watchTimes = watchTimesService.getOne(watchTimesId);
        data.put("watchTimes",watchTimes);
        if (watchTimes!= null){
            data.put("film",filmService.selectOne(watchTimes.getFilmId()));
            data.put("cinema",cinemaService.selectOne(watchTimes.getCmaId()));
        }

        responseBody.put("data",data);
        return responseBody;
    }
}
