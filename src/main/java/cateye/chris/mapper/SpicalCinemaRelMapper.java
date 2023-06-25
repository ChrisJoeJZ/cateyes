package cateye.chris.mapper;

import cateye.chris.bean.po.SpicalCinemaRel;
import cateye.chris.bean.po.SpicalCinemaRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SpicalCinemaRelMapper {
    long countByExample(SpicalCinemaRelExample example);

    int deleteByExample(SpicalCinemaRelExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SpicalCinemaRel record);

    int insertSelective(SpicalCinemaRel record);

    List<SpicalCinemaRel> selectByExample(SpicalCinemaRelExample example);

    SpicalCinemaRel selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SpicalCinemaRel record, @Param("example") SpicalCinemaRelExample example);

    int updateByExample(@Param("record") SpicalCinemaRel record, @Param("example") SpicalCinemaRelExample example);

    int updateByPrimaryKeySelective(SpicalCinemaRel record);

    int updateByPrimaryKey(SpicalCinemaRel record);
}