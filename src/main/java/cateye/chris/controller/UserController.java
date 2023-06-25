package cateye.chris.controller;

import cateye.chris.bean.bo.UserLoginBo;
import cateye.chris.bean.bo.UserRegistBo;
import cateye.chris.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    UserService userService;

    //用户注册手机号
    @ApiOperation(value = "用户注册")
    @PostMapping("/regist")
    public Object regist(UserRegistBo userRegistBo){ // 需要参数  手机号  密码 短信验证码
        Map<String,Object> responseBody = new HashMap<>();

        // 增加业务层代码，调用手机号注册 业务方法  进行注册
        int i = userService.regist(userRegistBo);
        String info="";
        if (i == 200){
            info="Registered successfully";
        } else if (i==500) {
            info="Registration failure";
        } else if (i==501) {
            info="Code verification failure";
        }
        //根据手机号注册返回的验证码，判断注册是否成功
        responseBody.put("code",i);
        responseBody.put("message",info);
        return responseBody;
    }

    /**
     * 用户登录
     * @param userLoginBo
     * @return 200：登陆成功； 500：账号不存在；  501：账户密码不正确
     */
    //用户登录
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public Object login(UserLoginBo userLoginBo){

        Map<String,Object> responseBody = new HashMap<>();
        //jwt生成的token
        String token = userService.Login(userLoginBo);
        int i;
        String info="";

        if (token.equals("")){
            i = 401;     //401,在http协议中，和用户登陆状态失效有关错误（标准协议）
            info="User name or login password is incorrect";
        }else {
            i=200;
            info="Login successfully";
            responseBody.put("data",token);       //登陆成功后告知用户/客户端，签发的令牌信息
        }

        //根据登陆验证方法，判断登录是否成功
        responseBody.put("code",i);
        responseBody.put("message",info);
        return responseBody;
    }
}
