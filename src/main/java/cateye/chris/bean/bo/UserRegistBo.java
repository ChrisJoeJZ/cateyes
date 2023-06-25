package cateye.chris.bean.bo;

import lombok.Data;

//业务模型类，负责用户注册业务
@Data
public class UserRegistBo {

    private String userLoginName;  //登录账户名（手机号）
    private String userLoginPass;  //注册密码（传过来是明文，放入mysql之后用密文）
    private String userNickName;   //昵称
    private Integer code;          //短信验证码
}
