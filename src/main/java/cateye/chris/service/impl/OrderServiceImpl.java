package cateye.chris.service.impl;

import cateye.chris.aop.annotation.TokenValid;
import cateye.chris.bean.bo.OrderAddBo;
import cateye.chris.bean.bo.SelectSeatBo;
import cateye.chris.bean.po.*;
import cateye.chris.bean.vo.SeatVo;
import cateye.chris.mapper.CinemaMapper;
import cateye.chris.mapper.FilmMapper;
import cateye.chris.mapper.OrdersMapper;
import cateye.chris.mapper.WatchTimesMapper;
import cateye.chris.service.OrderService;
import cateye.chris.utils.RedisUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//订单业务的业务层实现类
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    RedisUtils redisUtils;
    @Resource
    OrdersMapper ordersMapper;

    @Resource
    WatchTimesMapper watchTimesMapper;
    @Resource
    FilmMapper filmMapper;
    @Resource
    CinemaMapper cinemaMapper;

    @Resource
    KafkaTemplate<String,String> kafkaTemplate;
    @Override
    public boolean selectSeat(SelectSeatBo selectSeatBo, String token) {
        //构建往redis中存储的key，对于bo类进行一个JSON字符串转换
        String key = "seat-frozen-" + JSON.toJSONString(selectSeatBo);
        //从mysql（orderMapper）判断，该座位是否已经售出

        OrdersExample ordersExample = new OrdersExample();
        QueryWrapper<Object> wrapper = new QueryWrapper<>();

        ordersExample.createCriteria()              //创建条件查询
                .andOrderWtIdEqualTo(selectSeatBo.getWatchTimesId())         //第一个条件，根据场次ID查询
                .andOrderSitesLike("%"+selectSeatBo.getSeatId()+"%");  //增加第二个查询条件，座位ID的模糊查询

        List<Orders> list = ordersMapper.selectByExample(ordersExample);    //查询订单，得到符合条件的订单列表

        if (list != null && list.size()>=1){
            //说明该场次的该座位已经售出
            System.out.println("该座位已售出");
            return false;
        }


        //从redis判断，该座位是否被冻结（不管是其他用户/还是自己冻结，都无法再次冻结）
        if (redisUtils.hashKey(key)){
            return false;
        }

        //使用setnx指令，尝试对于 mutex（互斥锁）key进行分布式锁的争夺
        String mutexKey = "mutex-"+key;
        if (redisUtils.setnx(mutexKey,5) == false){
            //说明争夺分布式锁失败，（说明该场次该座位，有人正在占）
            return false;
        }

        //分布式锁争夺成功，需要往redis中存储，当前用户冻结座位
        boolean b = redisUtils.set(key, token, 60 * 15);

        //true, 设置成功了（比较难区分出到底是创建一个key成功 还是修改一个key成功）
        //setnx分布式锁（首次set一个key时候与set功能一样，但是当setnx一个已存在的值时会失败）

        //false 设置的时候失败了

        return b;
    }

    //生成订单的业务实现类
    @Override
    public boolean orderAdd(OrderAddBo orderAddBo, String token) {

        //第一步 查看当前用户的登陆状态
        UserInfo userInfo=null;
        //从传入的token参数，访问redis，得到一个Json的UserInfo实例
        Object tokenJson = redisUtils.get(token);
        //从json字符串转换成UserInfo类型的实例
        if (tokenJson !=null){
            userInfo = (UserInfo) tokenJson;
            if (userInfo == null){
                System.out.println("从json转换成UserInfo类型失败");
                return false;
            }

        }else {
            System.out.println("token转换失败，取不到当前登陆对象，生成订单失败");
            return false;     //token转换失败，取不到当前登陆对象，生成订单失败
        }
        

        //第二步：访问redis，查看所选择的座位是否都处于冻结状态（冻结的人是不是自己）
        List<SeatVo> list = JSONArray.parseArray( orderAddBo.getSeatList(),SeatVo.class );

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////防止出现一座多卖的情况，在拿到用户选择的座位列表后，要进行判断用户选中的该座位是否都在redis中处于冻结的状态/////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        for (SeatVo seatVo : list) {
            //需要先实例化一个SelectSeatBo类型
            SelectSeatBo selectSeatBo = new SelectSeatBo();
            selectSeatBo.setWatchTimesId(orderAddBo.getWatchTimesId());  //获取场次的编号
            selectSeatBo.setSeatId(seatVo.getSite_no());                 //获取座位编号
            //对于每个座位都需要建一个redis-key，判断这个redis-key在redis中是否存在
            String redisKey = "seat-frozen-"+JSON.toJSONString(selectSeatBo);

            if (redisUtils.hashKey(redisKey) == false){
                System.out.println("该座位在redis中不存在，座位未被冻结");
                return false;
            }
            //还要进行判断这个key的value是否时当前这个用户冻结的，如果不是应该返回错误
            Object redisValue = redisUtils.get(redisKey);
            if (!redisKey.toString().equals(token)){
                System.out.println("该座位并非当前用户冻结，已被其他用户冻结，请求失败");
                return false;
            }

        }


        // 发送消息给placeorderde的Topic，要生成订单+删除redis中的冻结座位///////////////////////////
        //需要传递的数据  场次id，座位id，token
        //参数：传入到哪个topic，key的值是什么（将orderAddBo转成json当作key传出去，value就传token）
        kafkaTemplate.send("placeorder",JSON.toJSONString(orderAddBo),token);

/*      中间包含的代码都由消费者渠道消息后代替执行
        //往order中插入数据
        Orders orders = new Orders();
        orders.setOrderNo(UUID.randomUUID().toString());
        orders.setOrderTime(new Date());
        orders.setOrderUserId(userInfo.getUserId());
        orders.setOrderUserNick(userInfo.getUserNickName());

        ///////////第三步：访问mysql 根据场次id得到场次实体类///////////////
        WatchTimes watchTimes = watchTimesMapper.selectByPrimaryKey(orderAddBo.getWatchTimesId());
        orders.setOrderCinemaId(watchTimes.getCmaId());
        //第四步 访问mysql根据影院id得到影院信息
        Cinema cinema = cinemaMapper.selectByPrimaryKey(watchTimes.getCmaId());
        orders.setOrderCinemaName(cinema.getCmaName());
        //第五步：访问mysql 根据电影id得到电影信息
        orders.setOrderFilmId(watchTimes.getFilmId());
        Film film = filmMapper.selectByPrimaryKey(watchTimes.getFilmId());
        orders.setOrderFilmName(film.getFilmName());

        orders.setOrderWdDate(watchTimes.getWdDate());        //设置场次日期
        orders.setOrderWtBegintime(watchTimes.getWtBegintime());
        orders.setOrderWtEndtime(watchTimes.getWtEndtime());
        orders.setOrderWtHalls(watchTimes.getWtHalls());      //设置放映厅类型

        //该订单总价的计算：该场次的单价*该用户选择的座位数的数量
        //场次的单价从watchTimes中取得 getWtCost
        //该用户该场次冻结的座位数量（用户冻结的那些座位会在redis中存放）seatList：从json转成List<seatVo>然后获取他的size也就是选了几个座位



        int seatCount = list.size();      //选中座位的个数
        orders.setOrderCost(new BigDecimal(seatCount * watchTimes.getWtCost().doubleValue()));  //将BigDecimal类型的weCost转为Double类型

        orders.setOrderSites(orderAddBo.getSeatList());    //设置订单中的座位信息

        orders.setOrderWtId(orderAddBo.getWatchTimesId());  //设置电影场次id
//        orders.setOrderIsUse();

        //第六步 访问mysql 记录生成的订单数据
        int affectedRows = ordersMapper.insert(orders);
        //判断插入的结果（受影响的条数大于零）
        //第七步  访问mysql  删除冻结的座位号
        if (affectedRows <= 0){
            System.out.println("添加订单信息返回的记录不合法，返回false");
            return false;
        }else {
            //调用redis，解除冻结座位数据
            for (SeatVo seatVo : list) {
                SelectSeatBo selectSeatBo = new SelectSeatBo();
                selectSeatBo.setWatchTimesId(orderAddBo.getWatchTimesId());
                selectSeatBo.setSeatId(orderAddBo.getSeatList());
                //订单中的每个座位生成一个key，用来删除redis中的key seat-frozen-josnstring
                String redisKey = "seat-frozen-"+JSON.toJSONString(selectSeatBo);
                //假如在删除过程中，因为redis服务器出现故障，导致删除动作失败，造成订单已经写入数据库，但是redis中并没有删除（给用户造成该座位还在冻结的假象）
                //会使用户看到座位依旧时已选择（绿色：已选择但是没有付款），但实际上自己已经付款了
                redisUtils.del(redisKey);
            }
        }

 */
        return true;


    }
}
