package cateye.chris.service;

import cateye.chris.bean.bo.OrderAddBo;
import cateye.chris.bean.bo.SelectSeatBo;

//订单类业务层接口类
public interface OrderService {

    //用户选座业务
    boolean selectSeat(SelectSeatBo selectSeatBo,String token);

    //生成订单业务，返回生成成功或是失败(选择的场次ID，座位号ID，当前用户登录ID)
    boolean orderAdd(OrderAddBo orderAddBo,String token);
}
