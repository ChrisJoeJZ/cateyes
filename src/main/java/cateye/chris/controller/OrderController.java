package cateye.chris.controller;

import cateye.chris.aop.annotation.TokenValid;
import cateye.chris.bean.bo.OrderAddBo;
import cateye.chris.bean.bo.SelectSeatBo;
import cateye.chris.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(tags = "座位相关")
@RequestMapping("/order")
public class OrderController {

    @Resource
    OrderService orderService;
    //选座的视图方法
    @TokenValid
    @ApiOperation(value = "选座相关方法")
    @PostMapping("/selectseat")
    public Object selectSeat(SelectSeatBo selectSeatBo, @RequestHeader String authorization){
        //场次编号（例202107300439940）。座位号（例1-4-10，1-4-9，1-4-8），获取authorization的参数数据用于校验当前用户登陆状态）
        //第一层级返回map数据
        Map<String, Object> responseBody = new HashMap<>();

        //从authorization中得到token数据
        String token = authorization.split(" ")[1];
        //从业务层调用selectSeat方法，判断是否选座成功
        boolean result = orderService.selectSeat(selectSeatBo,token);
        if (result){
            //选座成功
            responseBody.put("code",200);
            responseBody.put("message","success");
            responseBody.put("data",selectSeatBo);
            return responseBody;
        }else {
            //选座失败
            responseBody.put("code",500);
            responseBody.put("message","该座位已被其他用户占用，选座失败");
            return responseBody;
        }

        //视图方法，校验token的有效性新增一个判断登陆状态的aspect类

        //如果该座位已经被选中或售出，返回选座失败，让用户重新选座。

        //记录当前登录用户选中的座位。让其他用户暂时不能选择该场次的该座位
    }


    //生成订单业务的视图方法
    @TokenValid
    @GetMapping("/orderadd")
    @ApiOperation(value = "生成订单业务")
    //测试数据 场次ID：202108050347464   座位ID：000000018097-4-1或-2（可选）  000000018097-3-1（已售）
    public Object orderAdd(OrderAddBo orderAddBo, @RequestHeader String authorization){
        //构建第一层级返回数据
        Map<String,Object> responseBody = new HashMap<>();

        //从authorization中得到token的字符串
        String token = authorization.split(" ")[1];
        //调用业务层方法
        boolean b = orderService.orderAdd(orderAddBo, token);

        if (b == true){
            responseBody.put("code",200);
            responseBody.put("message","success");

            responseBody.put("data",orderAddBo);
        }else {
            responseBody.put("code",500);
            responseBody.put("message","Add order failed");
        }

        return responseBody;
    }

}
