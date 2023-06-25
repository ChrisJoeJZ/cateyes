package cateye.chris.mapper;

import cateye.chris.bean.po.Regions;
import cateye.chris.bean.po.RegionsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RegionsMapper {
    long countByExample(RegionsExample example);

    int deleteByExample(RegionsExample example);

    int deleteByPrimaryKey(Integer regionId);

    int insert(Regions record);

    int insertSelective(Regions record);

    List<Regions> selectByExample(RegionsExample example);

    Regions selectByPrimaryKey(Integer regionId);

    int updateByExampleSelective(@Param("record") Regions record, @Param("example") RegionsExample example);

    int updateByExample(@Param("record") Regions record, @Param("example") RegionsExample example);

    int updateByPrimaryKeySelective(Regions record);

    int updateByPrimaryKey(Regions record);
}