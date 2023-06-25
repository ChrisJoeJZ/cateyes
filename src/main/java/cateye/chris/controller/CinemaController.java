package cateye.chris.controller;

import cateye.chris.bean.bo.CinemaSearchBo;
import cateye.chris.service.BrandService;
import cateye.chris.service.ChinaService;
import cateye.chris.service.CinemaService;
import cateye.chris.service.SpecialHallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

//影院视图类
@Api(tags = "影院业务")
@RestController
@RequestMapping("/cinema")
public class CinemaController {
    @Resource
    BrandService brandService;

    @Resource
    SpecialHallService specialHallService;
    @Resource
    ChinaService chinaService;
    @Resource
    CinemaService cinemaService;

    //影院筛选功能的试图方法
    @ApiOperation(value = "查询影院列表")
    @PostMapping("/_search")
    public Object cinemaList(CinemaSearchBo cinemaSearchBo){
        //构建第一层需要返回的map
        Map<String,Object> responseBody = new HashMap<>();
        responseBody.put("code",200);
        responseBody.put("message","success");
        //第二层级的返回map，存放data的json成员属性中
        Map<String,Object> data = new HashMap<>();
        //返回当前所有影院品牌数据
        data.put("brandList",brandService.selectAll());
        //返回所有放映厅类型数据
        data.put("specialHallList",specialHallService);
        //返回当前用户所在的城市位置区域划分列表
        data.put("chinaList",chinaService.selectListByPid(310000));
        //返回查询条件bo
        data.put("cinemaSearchBo",cinemaSearchBo);
        //返回符合条件的影院列表
        data.put("cinemaList",cinemaService.selectByBo(cinemaSearchBo));

        responseBody.put("data",data);

        return responseBody;
    }

    //查询某家影院的数据
    @ApiOperation(value = "查询一家影院")
    @GetMapping("/{id}")
    public Object getOne(@PathVariable("id") Integer cinema_id){
        //构建第一层级的返回值
        Map<String,Object> responseBody =new HashMap<>();
        responseBody.put("code",200);
        responseBody.put("message","success");
        //data数据要包含单个影院信息+该影院正在上映的影片列表+该影院正在上映的一部电影的上映日期+该部电影的某日的拍片数据
        //--需要构建一个VO

        responseBody.put("data",cinemaService.selectVoById(cinema_id));

        return responseBody;
    }

}
