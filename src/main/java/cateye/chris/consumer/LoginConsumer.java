package cateye.chris.consumer;

import cateye.chris.bean.po.UserInfo;
import cateye.chris.bean.po.UserLoginLog;
import cateye.chris.mapper.UserInfoMapper;
import cateye.chris.mapper.UserLoginLogMapper;
import cateye.chris.utils.RedisUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//针对用户登录业务消息消费的消费者
//在实际项目中，往往生产者的代码程序和消费者的代码程序时分开到不同项目+部署到不同服务器上
@Component
public class LoginConsumer {

    @Resource
    UserLoginLogMapper userLoginLogMapper;
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    RedisUtils redisUtils;
    //需要接收到指定topic（loginlogs）的消息
    //ConsumerRecord泛型中的类型值需要与发出来的类型值保持一致
    @KafkaListener(topics = "loginlogs",groupId = "group-01") //java程序启动后（消费者的角色就会生效，从消息队列中拿到数据，输出出来）
    public void addLoginLogRedis(ConsumerRecord<String,String> record, Acknowledgment ack){

        //从指定的topic+topic的kafka队列中，接收数据（该队列中，只要有消息会被接受，在控制台中展现出来）
        String key = record.key();        //该消息的key值（这里指的是userid）
        String value = record.value();    //该消息的value（这里指的是token）
        System.out.println("key="+key);
        System.out.println("value="+value);

        //mysql的登录日志记录
        //额外新增记录一条用户的登录日志
        UserLoginLog userLoginLog = new UserLoginLog();
        userLoginLog.setUser_id(Integer.parseInt(key));
        int affectedRows = userLoginLogMapper.addLoginLog(userLoginLog);
        System.out.println("记录用户登录日志受影响的行数："+affectedRows);

        if (affectedRows>0){
            UserInfo user = userInfoMapper.selectByPrimaryKey(Integer.parseInt(key));
            //只有登录成功+记录登录日志成功，再去redis中存放
            //在redis中，存储用户登录之后的token数据，有效期设定为两个小时(key= token;value=user)
            redisUtils.set(value,user,60*60*2);
            System.out.println("success");

        }

        //redis的token记录

        //上述两件事，由java代码独立实现，需要继续研究kafka消息不重复，不丢失
        //保证登录的三个步骤（引入消息队列的用户登录流程）都是完整的：
        //1.校验用户名+密码（主项目中）
        //2.登陆成功后，后面的两个步骤（mysql写入+redis写入），发消息出去（主项目担当生产者职责）
        //3.消息流转带broker。 kafka服务器中

        ack.acknowledge();

    }

}
