package cateye.chris.mapper;

import cateye.chris.bean.po.WatchDays;
import cateye.chris.bean.po.WatchDaysExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WatchDaysMapper {
    long countByExample(WatchDaysExample example);

    int deleteByExample(WatchDaysExample example);

    int deleteByPrimaryKey(Integer wdId);

    int insert(WatchDays record);

    int insertSelective(WatchDays record);

    List<WatchDays> selectByExample(WatchDaysExample example);

    WatchDays selectByPrimaryKey(Integer wdId);

    int updateByExampleSelective(@Param("record") WatchDays record, @Param("example") WatchDaysExample example);

    int updateByExample(@Param("record") WatchDays record, @Param("example") WatchDaysExample example);

    int updateByPrimaryKeySelective(WatchDays record);

    int updateByPrimaryKey(WatchDays record);
}