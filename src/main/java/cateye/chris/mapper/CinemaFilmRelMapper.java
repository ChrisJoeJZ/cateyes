package cateye.chris.mapper;

import cateye.chris.bean.po.CinemaFilmRel;
import cateye.chris.bean.po.CinemaFilmRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CinemaFilmRelMapper {
    long countByExample(CinemaFilmRelExample example);

    int deleteByExample(CinemaFilmRelExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CinemaFilmRel record);

    int insertSelective(CinemaFilmRel record);

    List<CinemaFilmRel> selectByExample(CinemaFilmRelExample example);

    CinemaFilmRel selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CinemaFilmRel record, @Param("example") CinemaFilmRelExample example);

    int updateByExample(@Param("record") CinemaFilmRel record, @Param("example") CinemaFilmRelExample example);

    int updateByPrimaryKeySelective(CinemaFilmRel record);

    int updateByPrimaryKey(CinemaFilmRel record);
}