package cateye.chris.bean.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//用户登录模型
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginBo {

    private String userLoginName;
    private String userLoginPass;
}
