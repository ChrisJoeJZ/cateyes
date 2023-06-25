package cateye.chris.controller;

import cateye.chris.service.IMsgService;
import cateye.chris.utils.AliSMSClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
@Api(tags = "短信模块")
@RestController
@RequestMapping("/msg")
public class MsgController {


    @Resource
    IMsgService msgService;

    /**
     * 发送短信
     * @param phone  用户的手机号
     * @return restful风格的结果（200代表成功，500代表失败）
     */
    @ApiOperation(value = "发送注册短信")
    @PostMapping("/regist")
    public Object sendMsg(String phone){
        Map<String,Object> responseBody = new HashMap<>();

        //根据返回值，判断是否发送成功
        boolean b = msgService.sendRegistMsg(phone);
        if (b){
            responseBody.put("code",200);
            responseBody.put("message","success");
        }else {
            responseBody.put("code",500);
            responseBody.put("message","try again later");
        }


        return responseBody;
    }
}
