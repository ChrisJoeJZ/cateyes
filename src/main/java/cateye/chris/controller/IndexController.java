package cateye.chris.controller;

import cateye.chris.service.FilmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

//视图类-首页的视图类
@RestController   //将当前的类型托管给spring框架，转变成SpringBean(收益：spring框架中的IOC不用new的关键字，符合最小知识法则)
                  //当前的视图类是一个REST视图类， SpringRest技术栈（restful风格优势：通过请求链接更容易看懂）
@RequestMapping({"/index","/"})
@Api(tags = "电影网站首页")
public class IndexController {

    @Resource
    FilmService filmService;
    @ApiOperation(value = "首页")
    @GetMapping({"/index","/"})
    public Object index(){
        Map<String,Object> responseBody = new HashMap<>();
        responseBody.put("code",200);
        responseBody.put("message","success");
        responseBody.put("data",filmService.indexFilm());
        return responseBody;
    }



}
