package cateye.chris.service.impl;

import cateye.chris.aop.annotation.RedisCache;
import cateye.chris.bean.po.SpecialHall;
import cateye.chris.mapper.SpecialHallMapper;
import cateye.chris.service.SpecialHallService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecialHallServiceImpl implements SpecialHallService {

    @Resource
    SpecialHallMapper specialHallMapper;
    @RedisCache(duration = 60*60*24)
    @Override
    public List<SpecialHall> selectAll() {
        return specialHallMapper.selectByExample(null);
    }
}
