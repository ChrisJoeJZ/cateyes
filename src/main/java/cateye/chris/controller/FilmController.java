package cateye.chris.controller;

import cateye.chris.bean.bo.FilmSearchBo;
import cateye.chris.service.CategoryService;
import cateye.chris.service.CommentService;
import cateye.chris.service.FilmRegionService;
import cateye.chris.service.FilmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "电影模块")   //增加@API注解，标记为swagger接口
@RestController
@RequestMapping("/film")
public class FilmController {

    @Resource
    CategoryService categoryService;
    @Resource
    FilmRegionService filmRegionService;
    @Resource
    FilmService filmService;
    @Resource
    CommentService commentService;

    //全文索引的查询+从前端，postman发起的对于电影数据的检索
    @PostMapping("/_search")
    @ApiOperation(value="影片的筛选功能")
    public Object filmList(FilmSearchBo filmSearchBo){  //按照影片category，拍摄地，年代，关键字搜索进行筛选
        Map<String,Object> responseBody = new HashMap<>();
        responseBody.put("code",200);
        responseBody.put("message","success");

        //根据bo参数，调用对应service层方法读取符合条件的电影数据
        Map<String,Object> data = new HashMap<>();
        //存放所有的电影类型
        data.put("categoryList",categoryService.selectAll());      //从mysql中获取
        //存放所有的电影区域
        data.put("filmRegionList",filmRegionService.selectAll());    //从mysql中获取
        //存放符合当前搜索条件的电影数据
        data.put("filmSearchBo",filmSearchBo);
        //存放符合当前条件的电影列表数据
        data.put("filmList",filmService.selectByBo(filmSearchBo));          //从es中获取

        responseBody.put("data",data);       //data中要存放所有电影类型，电影区域，符合当前搜索条件的数据，以及符合条件的电影列表
        return responseBody;
    }

    //影片详情页面的数据 Url格式film/filmId

    @GetMapping("/{id}")
    @ApiOperation(value = "查询一部影片的详情信息，含影片的评论列表")
    public Object getOne(@PathVariable("id") Integer film_id){  //从url的path中取到电影的id
        Map<String,Object> responseBody= new HashMap<>();
        responseBody.put("code",200);
        responseBody.put("message","success");
        //构建第二层及的json对象，放到data的json成员对象中
        Map<String,Object> data = new HashMap<>();
        data.put("film",filmService.selectOne(film_id));        //影片信息
        data.put("commonList",commentService.selectListByFilmId(film_id));  //存放电影的评论信息


        responseBody.put("data",data);

        return responseBody;
    }

}
