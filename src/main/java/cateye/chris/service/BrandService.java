package cateye.chris.service;

import cateye.chris.bean.po.Brand;

import java.util.List;

public interface BrandService {
    //返回所有影院列表的方法
    List<Brand> selectAll();

}
