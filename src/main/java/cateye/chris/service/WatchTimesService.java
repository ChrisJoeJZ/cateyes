package cateye.chris.service;


import cateye.chris.bean.po.WatchTimes;

public interface WatchTimesService {

    //根据场次id，获取该场次的详细信息
    WatchTimes getOne(String watchTimesId);
}
