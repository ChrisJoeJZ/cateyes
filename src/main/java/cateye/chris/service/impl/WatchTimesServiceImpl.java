package cateye.chris.service.impl;

import cateye.chris.bean.po.WatchTimes;
import cateye.chris.mapper.WatchTimesMapper;
import cateye.chris.service.WatchTimesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WatchTimesServiceImpl implements WatchTimesService {

    @Resource
    WatchTimesMapper watchTimesMapper;
    @Override
    public WatchTimes getOne(String watchTimesId) {
        return watchTimesMapper.selectByPrimaryKey(watchTimesId);
    }
}
