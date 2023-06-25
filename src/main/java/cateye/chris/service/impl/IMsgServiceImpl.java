package cateye.chris.service.impl;

import cateye.chris.service.IMsgService;
import cateye.chris.utils.RedisUtils;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IMsgServiceImpl implements IMsgService {

    @Resource
    Client client;

    //注入redis工具类
    @Resource
    RedisUtils redisUtils;

    @Override
    public boolean sendRegistMsg(String phone) {

        //生成一个六位的随机数字（下发的验证码）-- 取值范围在100000-999999之间的数字
        int code = (int) (Math.random() * (999999 - 100000 + 1) + 100000);

        //构造一个SendSmsRequest实体类，作为阿里云短信的调用方法的参数
        // 创建 发送短信的请求对象
        SendSmsRequest request = new SendSmsRequest();
        // 设置接收短信的手机号码
        request.setPhoneNumbers(phone);
        // 设置短信签名（企业申请）
        request.setSignName("学掌门超全栈开发");
        // 设置短信模板（企业申请）
        request.setTemplateCode("SMS_243437190");
        // 设置短信模板中的变量参数
        request.setTemplateParam("{\"code\":\"" + code + "\"}");

        //调用client.sendSmsWithOptions 阿里云的发送短信接口，完成该试图方法
        //一个response的结果，判断发送成功还是失败

        try {
            // 发送短信请求 得到 短信响应
            SendSmsResponse response = client.sendSmsWithOptions( request , new RuntimeOptions() );
            // 判断 短信发送是否成功
            if( "OK".equalsIgnoreCase( response.getBody().getCode() ) ){
                // 短信发送成功...
                //调用redis的set功能，存放短信验证码的数据（key: meg-手机号； value：短信验证码）
                String key = "msg-" + phone;
                long expired = 30*60;       //30分钟后验证码过期
                redisUtils.set(key,code,expired);
                return true;
            }else{
                // 短信发   送失败...
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
