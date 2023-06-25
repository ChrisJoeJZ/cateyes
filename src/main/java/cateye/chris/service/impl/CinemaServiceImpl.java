package cateye.chris.service.impl;

import cateye.chris.aop.annotation.RedisCache;
import cateye.chris.bean.bo.CinemaSearchBo;
import cateye.chris.bean.po.Cinema;
import cateye.chris.bean.po.Film;
import cateye.chris.bean.po.WatchTimes;
import cateye.chris.bean.vo.CinemaVo;
import cateye.chris.bean.vo.FilmVo;
import cateye.chris.bean.vo.WatchDateVo;
import cateye.chris.mapper.CinemaMapper;
import cateye.chris.mapper.FilmMapper;
import cateye.chris.mapper.WatchTimesMapper;
import cateye.chris.service.CinemaService;
import cateye.chris.utils.ESResponse;
import cateye.chris.utils.ESUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CinemaServiceImpl implements CinemaService {

    @Resource
    ESUtils esUtils;
    @Resource
    CinemaMapper cinemaMapper;

    @Resource
    FilmMapper filmMapper;

    @Resource
    WatchTimesMapper watchTimesMapper;

    //影院的筛选功能（最好能加上redis缓存）

    @RedisCache(duration = 60*60*24)  //缓存一天
    @Override
    public List<Cinema> selectByBo(CinemaSearchBo cinemaSearchBo) {
        //构建一个筛选对象
        BoolQueryBuilder query = new BoolQueryBuilder();
        //根据影院品牌筛选（Integer brandId）
        if (cinemaSearchBo.getBrandId() != null){
            query.should(QueryBuilders.termQuery("brandId",cinemaSearchBo.getBrandId()));
        }
        //根据放映厅类型进行筛选（cma_hall_query 往es插入数据时，按照逗号分隔的分词查询即可）
        if (cinemaSearchBo.getSpecialHallId()!= null){
            query.must(QueryBuilders.termQuery("cma_hall_query",cinemaSearchBo.getSpecialHallId()));
        }
        //根据服务进行筛选(cma_service_query，往es插入数据时，按照逗号分隔的分词查询即可)
//        if (cinemaSearchBo.getServiceId()!= null){
//            query.must(QueryBuilders.termQuery("cma_service_query",cinemaSearchBo.getServiceId()));
//        }
        //根据行政区域进行筛选(cma_address,模糊查询,相当于like子句 %地址%)
        if (cinemaSearchBo.getChinaName()!=null && !cinemaSearchBo.getChinaName().isBlank() ){
            query.must(QueryBuilders.matchQuery("cma_address",cinemaSearchBo.getChinaName()));
        }
        //根据关键字进行筛选
        if (cinemaSearchBo.getSearch()!=null && !cinemaSearchBo.getSearch().isBlank() ){
            query.must(QueryBuilders.matchQuery("cma_name",cinemaSearchBo.getSearch()));
        }
        //构建一个搜索条件（筛选对象放置到搜索条件中）
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //给搜索条件增加筛选功能
        searchSourceBuilder.query(query);
        //排序功能
        if (cinemaSearchBo.getOrderColumn()!=null && !cinemaSearchBo.getOrderColumn().isBlank()){
            searchSourceBuilder.sort(cinemaSearchBo.getOrderColumn(),
                    cinemaSearchBo.getOrderType()!=null && cinemaSearchBo.getOrderType().equals("desc") ? SortOrder.DESC:SortOrder.ASC
            );
        }

        //分页功能
        // 搜索建造者 设置 分页条件 => limit 子句
        searchSourceBuilder.from( Integer.parseInt( cinemaSearchBo.getStartIndex().toString() ) );
        searchSourceBuilder.size( Integer.parseInt( cinemaSearchBo.getPageSize().toString() ) );

        //调用es的工具操作类，完成数据的返回
        ESResponse esResponse = esUtils.search(
                "cinema",      //要查询的es的Index（数据结构）
                searchSourceBuilder,    //搜索条件
                Cinema.class            //查询结果转换成指定的对象类型
        );
        //设置esresponse的总记录数（VUE的层面可用于分页）
        cinemaSearchBo.setResultCount(esResponse.getResultCount());
        System.out.println(esResponse.getResultCount());

        return esResponse.getData();
    }

    @Override
    public CinemaVo selectVoById(Integer cinema_id) {
        //实例化一个vo对象
        CinemaVo cinemaVo = new CinemaVo();
        //访问数据库，根据id得到影院的数据
        Cinema cinema = cinemaMapper.selectByPrimaryKey(cinema_id);
        if (cinema!= null){
            //赋值给vo中的成员属性
            cinemaVo.setCinema(cinema);
        }
        //访问数据库，根据影院id，渲染该影院正在上映的影片列表数据
        List<FilmVo> filmList = new ArrayList<>();       //实例化一个动态数组
        List<Film> list= filmMapper.selectListByCmaId(cinema_id);
        if (list!=null){
            //需要额外便利list的数据
            for (Film film : list) {
                FilmVo filmVo = new FilmVo();
                filmVo.setFilm(film);          //实例化一个filmVo
                //影片的拍片日期
                List<WatchDateVo> watchDateVoList = new ArrayList<>();
                List<String> watchDateList = watchTimesMapper.selectWathcDateList(cinema_id,film.getFilmId());

                for (String s : watchDateList) {
                    WatchDateVo watchDateVo = new WatchDateVo();
                    watchDateVo.setDate(s);     //设置拍片日期vo的日期
                    //需要得到该影院+该影片+该日期的拍片数据
                    watchDateVo.setWatchTimesList(watchTimesMapper.selectList(cinema_id,film.getFilmId(),s));
                    watchDateVoList.add(watchDateVo);
                }

                filmVo.setWatchDateList(watchDateVoList);

                filmList.add(filmVo);          //赋值filmVo实体成员属性
            }
            cinemaVo.setFilmList(filmList);
        }
        return cinemaVo;
    }

    @Override
    public Cinema selectOne(Integer cinema_id) {
        return cinemaMapper.selectByPrimaryKey(cinema_id);
    }
}
