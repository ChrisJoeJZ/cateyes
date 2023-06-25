package cateye.chris.service.impl;

import cateye.chris.aop.annotation.RedisCache;
import cateye.chris.bean.po.China;
import cateye.chris.mapper.ChinaMapper;
import cateye.chris.service.ChinaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChinaServiceImpl implements ChinaService {
    @Resource
    ChinaMapper chinaMapper;
    @RedisCache(duration = 60*60*24)
    @Override
    public List<China> selectListByPid(Integer pid) {
        return chinaMapper.selectListByPid(pid);
    }
}
