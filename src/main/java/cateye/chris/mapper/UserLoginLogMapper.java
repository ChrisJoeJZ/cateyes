package cateye.chris.mapper;

import cateye.chris.bean.po.UserLoginLog;
import org.apache.ibatis.annotations.Param;

public interface UserLoginLogMapper {
    //记录用户登录的日志
    int addLoginLog(@Param("userLoginLog") UserLoginLog userLoginLog);

}
