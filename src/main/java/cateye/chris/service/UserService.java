package cateye.chris.service;

import cateye.chris.bean.bo.UserLoginBo;
import cateye.chris.bean.bo.UserRegistBo;

//业务层接口类，-负责用户业务（注册+登录）
public interface UserService {

    //用户注册
    //需要传入的数据参数是什么样的(手机号，密码，昵称，短信验证码)
    //返回值 200：成功。 500：失败、 501：短信验证码校验不通过失败
    int regist(UserRegistBo userRegistBo);

    //用户登陆方法
    String Login(UserLoginBo userLoginBo);
}
