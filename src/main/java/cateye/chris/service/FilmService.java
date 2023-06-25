package cateye.chris.service;

import cateye.chris.bean.bo.FilmSearchBo;
import cateye.chris.bean.po.FilmWithBLOBs;

import java.util.List;
import java.util.Map;

//电影的业务接口类
public interface FilmService {
    //为网站的首页提供数据
    Map<String,Object> indexFilm();

    //查询满足条件的电影列表
    List<FilmWithBLOBs> selectByBo(FilmSearchBo filmSearchBo);

    //查询某个影片的详情
    FilmWithBLOBs selectOne(Integer film_id);
}
