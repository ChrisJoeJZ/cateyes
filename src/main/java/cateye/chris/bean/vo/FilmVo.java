package cateye.chris.bean.vo;

import cateye.chris.bean.po.Film;
import lombok.Data;

import java.util.List;

//影片相关的视图模型类
@Data
public class FilmVo {
    private Film film;   //影片的实体类
    private List<WatchDateVo> watchDateList;   //放映日期列表
}
