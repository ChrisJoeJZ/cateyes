package cateye.chris.service;

import cateye.chris.bean.po.China;

import java.util.List;

//行政地区业务层
public interface ChinaService {
    List<China> selectListByPid(Integer pid);
}
