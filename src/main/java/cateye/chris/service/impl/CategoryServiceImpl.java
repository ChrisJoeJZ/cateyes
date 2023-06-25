package cateye.chris.service.impl;

import cateye.chris.bean.po.Category;
import cateye.chris.mapper.CategoryMapper;
import cateye.chris.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    CategoryMapper categoryMapper;
    @Override
    public List<Category> selectAll() {
        return categoryMapper.selectByExample(null);
    }
}
