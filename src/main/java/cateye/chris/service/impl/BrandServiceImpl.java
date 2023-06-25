package cateye.chris.service.impl;

import cateye.chris.aop.annotation.RedisCache;
import cateye.chris.bean.po.Brand;
import cateye.chris.mapper.BrandMapper;
import cateye.chris.service.BrandService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class BrandServiceImpl implements BrandService {
    @Resource
    BrandMapper brandMapper;

    @RedisCache(duration = 60*60*24)      //缓存一天 时间
    @Override
    public List<Brand> selectAll() {
        return brandMapper.selectByExample(null);
    }
}
