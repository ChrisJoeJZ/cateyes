package cateye.chris.service.impl;

import cateye.chris.aop.annotation.RedisCache;
import cateye.chris.bean.bo.FilmSearchBo;
import cateye.chris.bean.po.FilmWithBLOBs;
import cateye.chris.mapper.FilmMapper;
import cateye.chris.service.FilmService;
import cateye.chris.utils.ESResponse;
import cateye.chris.utils.ESUtils;
import cateye.chris.utils.RedisUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//电影业务层实现类
@Service
public class FilmServiceImpl implements FilmService {

    @Resource
    private FilmMapper filmMapper;

    @Resource
    RedisUtils redisUtils;
    @Resource
    ESUtils esUtils;
    @RedisCache(duration = 60*60)  //用新增注解来实现redis缓存，缓存持续时间一小时
    @Override
    public Map<String, Object> indexFilm() {
        Map<String,Object> data = new HashMap<>();

//        String redisKey = "indexPage";
        //尝试从redis中取“indexPage”的数据 (用aop注解代替)
//        Object redisData = redisUtils.get(redisKey);
//        //取到数据，直接返回
//        if (redisData != null){
//            System.out.println("===> Redis缓存中，直接返回缓存数据");
//            data = (Map<String, Object>) redisData;
//            return data;
//        }

        //没取到，再访问mysql数据库
        FilmSearchBo bo = new FilmSearchBo();
        bo.setPage(1);            //默认显示页数
        bo.setPageSize(8);        //前三分list数据，默认pageSize是8
        //正在热映（电影列表至少八条+记录数）
        bo.setTypeId(1);
        data.put("currentList",filmMapper.selectByBo(bo));
        data.put("currentCount",filmMapper.selectCountByBo(bo));
        //即将上映
        bo.setTypeId(2);
        data.put("nextList",filmMapper.selectByBo(bo));
        data.put("nextCount",filmMapper.selectCountByBo(bo));

        //热播电影
        bo.setTypeId(3);
        data.put("hotList",filmMapper.selectByBo(bo));
        data.put("hotCount",filmMapper.selectCountByBo(bo));

        //想看人数倒叙（至少十条）
        bo.setOrderColumn("film_favorite");
        bo.setOrderType("desc");
        bo.setPageSize(10);        //后三份list数据,pageSize为10

        //调用符合该条件的记录数-访问层接口
//        resultCount = filmMapper.selectCountByBo(bo);
//        data.put("currentCount",resultCount);
        //返回符合条件的数据列表
        data.put("favoriteList",filmMapper.selectByBo(bo));


        //电影评分倒叙
        bo.setOrderColumn("film_praise");
        bo.setOrderType("desc");
        data.put("praiseList",filmMapper.selectByBo(bo));

        //对于数据库中查询到的数据存放到redis中，让数据缓存一个小时
//        redisUtils.set(redisKey,data,60*60);
        
        return data;
    }


    @Override
    public List<FilmWithBLOBs> selectByBo(FilmSearchBo filmSearchBo) {

        //构建一个筛选条件（film_id，类似于where film_id = ...）
        BoolQueryBuilder query = new BoolQueryBuilder();
//        query.should(
//                QueryBuilders.termQuery("film_id",718)   //不走分词器的查询
//        );  //常用
        //影片cate筛选（must代表走分词器的查询  must+termQuery连用）,需要额外判断categoryId是否不为空
        if (filmSearchBo.getCategoryId() != null){
            query.should(
                    QueryBuilders.termQuery("film_cate_query",filmSearchBo.getCategoryId())
            );
        }
        //拍摄地筛选
        if (filmSearchBo.getFilmRegionId() != null) {
            query.must(
                    QueryBuilders.termQuery("film_region_query",filmSearchBo.getFilmRegionId())
            );
        }

        //上映年份筛选
        if (filmSearchBo.getFilmYears() != null && !filmSearchBo.getFilmYears().isBlank()) {
            query.must(
                    QueryBuilders.termQuery("film_years",filmSearchBo.getFilmYears())
            );  //termQuery是不需要走分词器的查询
        }

        //关键字筛选
        if (filmSearchBo.getSearch() != null && !filmSearchBo.getSearch().isBlank()) {
            query.must(
                    QueryBuilders.matchQuery("film_name",filmSearchBo.getSearch())
            );  //matchQuery是需要走分词器的查询
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();   //搜索条件实例化
        //给搜索条件增加筛选功能（相当于mysql中where子句）
        searchSourceBuilder.query(query);
        //给搜索条件增加排序功能（相当于mysql中的order by子句）
        if (filmSearchBo.getOrderColumn()!=null && !filmSearchBo.getOrderColumn().isBlank()){
            searchSourceBuilder.sort(filmSearchBo.getOrderColumn(),
                    filmSearchBo.getOrderType()!=null && filmSearchBo.getOrderType().equals("desc") ? SortOrder.DESC:SortOrder.ASC
            );
        }

        //给搜索条件设置分页功能（相当于mysql中limit子句）
        searchSourceBuilder.from( Integer.parseInt( filmSearchBo.getStartIndex().toString() ) );
        searchSourceBuilder.size( Integer.parseInt( filmSearchBo.getPageSize().toString() ) );

        // 使用 ElasticSearch 操作帮助类 发送查询 得到结果（Film.class可自行替换，要与整个方法的泛型保持一致）
        ESResponse esResponse = esUtils.search( "film" ,  //要查询es中数据结构-期望查询电影列表数据
                                               searchSourceBuilder ,    //搜索的条件
                                               FilmWithBLOBs.class );   //查询到结果进行ORM映射的实体类型

        // 获取 EsResponse 对象中的 总记录数（用于分页）
        filmSearchBo.setResultCount( esResponse.getResultCount() );
        System.out.println( esResponse.getResultCount() );
        // 获取 EsResponse 对象中的 实体模型集合
        return esResponse.getData();
    }

    //按照影片的id，查询某部电影的详细信息
    @Override
    public FilmWithBLOBs selectOne(Integer film_id) {
        //从es中取数据的收益--命中次数越高会队影片的权重排名有积极的影响
        //构建一个筛选条件
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.should(QueryBuilders.termQuery("film_id",film_id));
        //使用es操作帮助类 发送查询得到数据
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);

        ESResponse<FilmWithBLOBs> esResponse = esUtils.search("film",searchSourceBuilder,FilmWithBLOBs.class);

        return esResponse.getData().size()>0? esResponse.getData().get(0):null;
    }
}
