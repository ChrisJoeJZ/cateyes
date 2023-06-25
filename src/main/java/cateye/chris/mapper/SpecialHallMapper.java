package cateye.chris.mapper;

import cateye.chris.bean.po.SpecialHall;
import cateye.chris.bean.po.SpecialHallExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SpecialHallMapper {
    long countByExample(SpecialHallExample example);

    int deleteByExample(SpecialHallExample example);

    int deleteByPrimaryKey(Integer specialId);

    int insert(SpecialHall record);

    int insertSelective(SpecialHall record);

    List<SpecialHall> selectByExample(SpecialHallExample example);

    SpecialHall selectByPrimaryKey(Integer specialId);

    int updateByExampleSelective(@Param("record") SpecialHall record, @Param("example") SpecialHallExample example);

    int updateByExample(@Param("record") SpecialHall record, @Param("example") SpecialHallExample example);

    int updateByPrimaryKeySelective(SpecialHall record);

    int updateByPrimaryKey(SpecialHall record);
}