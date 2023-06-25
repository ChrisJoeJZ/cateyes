package cateye.chris.bean.po;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserLoginLog {

    private Integer log_id;   //新增登陆记录的id
    private Integer user_id;   //登录用户id
    private Date log_time = new Date();   //登陆时间
    private Integer account_type = 2;  //账号类型  1：字符账号  2：手机账号


}
