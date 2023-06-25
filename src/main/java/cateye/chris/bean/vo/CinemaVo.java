package cateye.chris.bean.vo;

import cateye.chris.bean.po.Cinema;
import cateye.chris.bean.po.Film;
import lombok.Data;

import java.util.List;

//影院视图模型类
//包含单个影院信息+该影院正在上映的影片列表+该影院正在上映的一部电影的上映日期+该部电影的某日的拍片数据
@Data
public class CinemaVo {
    //成员属性要包含单个影院信息+该影院正在上映的影片列表+该影院正在上映的一部电影的上映日期+该部电影的某日的拍片数据
    private Cinema cinema;  //影院的书体模型对象
    private List<FilmVo> filmList;    //该影院正在上映的影片集合
}
