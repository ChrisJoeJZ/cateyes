package cateye.chris.mapper;

import cateye.chris.bean.po.RegionCmaRel;
import cateye.chris.bean.po.RegionCmaRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RegionCmaRelMapper {
    long countByExample(RegionCmaRelExample example);

    int deleteByExample(RegionCmaRelExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RegionCmaRel record);

    int insertSelective(RegionCmaRel record);

    List<RegionCmaRel> selectByExample(RegionCmaRelExample example);

    RegionCmaRel selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") RegionCmaRel record, @Param("example") RegionCmaRelExample example);

    int updateByExample(@Param("record") RegionCmaRel record, @Param("example") RegionCmaRelExample example);

    int updateByPrimaryKeySelective(RegionCmaRel record);

    int updateByPrimaryKey(RegionCmaRel record);
}