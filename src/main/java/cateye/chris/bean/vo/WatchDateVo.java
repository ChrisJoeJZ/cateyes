package cateye.chris.bean.vo;

import cateye.chris.bean.po.WatchTimes;
import lombok.Data;

import java.util.List;

//观影日期的实体模型类
@Data
public class WatchDateVo {
    private String date;   //观影日期
    private List<WatchTimes> watchTimesList;
}
