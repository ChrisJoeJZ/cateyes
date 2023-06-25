package cateye.chris.service.impl;

import cateye.chris.aop.annotation.RedisCache;
import cateye.chris.bean.po.FilmRegion;
import cateye.chris.mapper.FilmRegionMapper;
import cateye.chris.service.FilmRegionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class FilmRegionServiceImpl implements FilmRegionService {
    @Resource
    FilmRegionMapper filmRegionMapper;
    @RedisCache(duration = 60*60*24)
    @Override
    public List<FilmRegion> selectAll() {
        return filmRegionMapper.selectByExample(null);
    }
}
