package cateye.chris.mapper;

import cateye.chris.bean.po.China;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChinaMapper {
    //通过父级pid，查询下一层符合条件的行政地区数据
    //select * from China c where pid = ?
    List<China> selectListByPid(@Param("pid") Integer pid);
}
