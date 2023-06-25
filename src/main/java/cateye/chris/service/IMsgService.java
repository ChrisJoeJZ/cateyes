package cateye.chris.service;


//短信业务接口类

public interface IMsgService {

    /**
     * 发送短信验证码（需要在java服务端存放刚生成短信验证码的code
     * @param phone 发送短信到额手机号
     * @return 调用阿里云发送短信，返回一个boolean类型结果判断是否成功发送短信
     */
    boolean sendRegistMsg(String phone);
}
