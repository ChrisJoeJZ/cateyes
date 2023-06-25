package cateye.chris.service.impl;

import cateye.chris.bean.bo.UserLoginBo;
import cateye.chris.bean.bo.UserRegistBo;
import cateye.chris.bean.po.UserInfo;
import cateye.chris.bean.po.UserInfoExample;
import cateye.chris.bean.po.UserLoginLog;
import cateye.chris.mapper.UserInfoMapper;
import cateye.chris.mapper.UserLoginLogMapper;
import cateye.chris.service.UserService;
import cateye.chris.utils.RedisUtils;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    RedisUtils redisUtils;

    //用户登录日志
    @Resource
    UserLoginLogMapper userLoginLogMapper;

    @Resource
    private KafkaTemplate<String,String> kafkaTemplate;     //消息的key和value
    @Override
    public int regist(UserRegistBo userRegistBo) {
        // 第一步 用户提交的短信验证码和服务端的是否一致
        //从redis中读取code数据（手机号也就是UserLoginName）
        String key = "msg-"+userRegistBo.getUserLoginName();
        //加一层判断，避免出现空指针异常
        String redisCode = "";
        if (redisCode != null){
            redisCode = redisUtils.get(key).toString();
        }
        //验证通过继续下一步，验证码错误，返回501
        if (redisCode.equals(userRegistBo.getCode().toString())){
            //一致，通过校验
            System.out.println("verification successfully");
            redisUtils.del(key);
        }else {
            //不一致，校验失败
            System.out.println("Verification failure,redisCode="+redisCode+",用户提交的code="+userRegistBo.getCode()+"Stop registering");
            return 501;
        }

        //第二步。把用户注册信息存入mysql中（调用userINfoMapper.insert方法）
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginName(userRegistBo.getUserLoginName());
        userInfo.setUserNickName(userRegistBo.getUserNickName());
        //使用UUID为每个用户生成不同的盐值
        String salt = UUID.randomUUID().toString();
        userInfo.setUserSalt(salt);              //使用java生成盐值

        //使用MD5将用户注册的明文密码转为密文
        String cryptoPass;
        cryptoPass= DigestUtils.md5DigestAsHex( (userRegistBo.getUserLoginPass() + salt ).getBytes());
        userInfo.setUserLoginPass(cryptoPass);        //从明文转换成密文（md5+盐）
        userInfo.setUserEmail("");
        userInfo.setUserEnable((byte) 1);

        int i = userInfoMapper.insert(userInfo);

        if (i>=0){
            //注册成功
            return 200;
        }else {
            //注册失败
            return 500;
        }

    }

    /**
     * 用户登录
     * @param userLoginBo
     * @return 字符串JWT，如果返回 ”“，是失败的，返回JWT Token是成功登录
     */
    @Override
    public String Login(UserLoginBo userLoginBo) {
        //校验userLoginBo传入的数据与数据库中与存在的数据进行对比
        //可调用自带方法selectByExample方法进行对比
        //先实例化userInfoExample，相当于查询语句的where条件
        UserInfoExample example = new UserInfoExample();
        example.createCriteria()
                .andUserLoginNameEqualTo( userLoginBo.getUserLoginName() );

        List<UserInfo> list = userInfoMapper.selectByExample(example);
        //判断list是否存在数据，然后在进行密码是否正确判断
        if (list != null && list.size()>=1){
            //判断用户输入的密码是否正确
            String cryptoPass;     //用户登陆时输入的密码
            String salt = list.get(0).getUserSalt();
            String dbCryptoPass;   //数据库中用户注册时的密码

            cryptoPass = DigestUtils.md5DigestAsHex(((userLoginBo.getUserLoginPass()).toString() + salt).getBytes());
            dbCryptoPass = list.get(0).getUserLoginPass();
            if (cryptoPass.equals(dbCryptoPass)){
                //登录成功后，先借助JWT技术栈，签发jwt的token,返回生成的token
                UserInfo user = list.get(0);

                // 使用 JWT（JSon Web Token） 组件  签发令牌
                String token = JWT.create()     // 创建令牌
                        .withAudience( JSON.toJSONString(user) )    // 载荷 当前登录成功的用户对象（转化成json字符串）
                        .withIssuedAt( new Date() ) // 载荷 令牌的签发时间（当前时间）
                        .withExpiresAt( new Date( System.currentTimeMillis() + 1000 * 60 * 60 * 2) )   // 载荷 令牌的到期时间（120分钟以后）
                        .sign( Algorithm.HMAC256( user.getUserId().toString() ) );     // 使用秘钥签发令牌

                ////////后续代码需要由消费者来完成，发送一个kafka消息出去//////////////////////////////////
                kafkaTemplate.send("loginlogs",user.getUserId().toString(),token);   //参数内容：发送到哪个topic中，消息的key和value

                ///////后续注释掉的几行代码应该交给消费者去完成////////////////////////////////////

                //额外新增记录一条用户的登录日志
//                UserLoginLog userLoginLog = new UserLoginLog();
//                userLoginLog.setUser_id(user.getUserId());
//
//                int affectedRows = userLoginLogMapper.addLoginLog(userLoginLog);
//                System.out.println("记录用户登录日志受影响的行数："+affectedRows);
//                //只有登录成功+记录登录日志成功，再去redis中存放token数据
//
//                //在redis中，存储用户登录之后的token数据，有效期设定为两个小时(key= token;value=user)
//                redisUtils.set(token,user,60*60*2);

                return token;
            }else {
                return "";
            }
        }else {
            return "";
        }
    }
}
