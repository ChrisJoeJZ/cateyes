package cateye.chris.service;

import cateye.chris.bean.bo.CinemaSearchBo;
import cateye.chris.bean.po.Cinema;
import cateye.chris.bean.vo.CinemaVo;

import java.util.List;

//影院业务层
public interface CinemaService {
    //查询满足条件的影院列表
    List<Cinema> selectByBo(CinemaSearchBo cinemaSearchBo);

    //通过影院id查询影院的VO类
    CinemaVo selectVoById(Integer cinema_id);

    //通过影院id，查询影院详细信息
    Cinema selectOne(Integer cinema_id);
}
