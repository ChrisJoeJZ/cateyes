package cateye.chris.mapper;

import cateye.chris.bean.po.FilmRegion;
import cateye.chris.bean.po.FilmRegionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FilmRegionMapper {
    long countByExample(FilmRegionExample example);

    int deleteByExample(FilmRegionExample example);

    int deleteByPrimaryKey(Integer filmReId);

    int insert(FilmRegion record);

    int insertSelective(FilmRegion record);

    List<FilmRegion> selectByExample(FilmRegionExample example);

    FilmRegion selectByPrimaryKey(Integer filmReId);

    int updateByExampleSelective(@Param("record") FilmRegion record, @Param("example") FilmRegionExample example);

    int updateByExample(@Param("record") FilmRegion record, @Param("example") FilmRegionExample example);

    int updateByPrimaryKeySelective(FilmRegion record);

    int updateByPrimaryKey(FilmRegion record);
}