package cateye.chris.mapper;

import cateye.chris.bean.po.FilmFlregionRel;
import cateye.chris.bean.po.FilmFlregionRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FilmFlregionRelMapper {
    long countByExample(FilmFlregionRelExample example);

    int deleteByExample(FilmFlregionRelExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(FilmFlregionRel record);

    int insertSelective(FilmFlregionRel record);

    List<FilmFlregionRel> selectByExample(FilmFlregionRelExample example);

    FilmFlregionRel selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") FilmFlregionRel record, @Param("example") FilmFlregionRelExample example);

    int updateByExample(@Param("record") FilmFlregionRel record, @Param("example") FilmFlregionRelExample example);

    int updateByPrimaryKeySelective(FilmFlregionRel record);

    int updateByPrimaryKey(FilmFlregionRel record);
}