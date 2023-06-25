package cateye.chris.service;

import cateye.chris.bean.po.Category;

import java.util.List;

public interface CategoryService {
    //拆线呢所有电影的类型
    List<Category> selectAll();
}
