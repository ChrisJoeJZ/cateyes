package cateye.chris.service;

import cateye.chris.bean.po.SpecialHall;

import java.util.List;

public interface SpecialHallService {

    //返回所有类型放映厅
    List<SpecialHall> selectAll();
}
