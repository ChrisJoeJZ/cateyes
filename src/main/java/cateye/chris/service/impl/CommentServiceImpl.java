package cateye.chris.service.impl;

import cateye.chris.bean.po.Comment;
import cateye.chris.service.CommentService;
import cateye.chris.utils.ESResponse;
import cateye.chris.utils.ESUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    ESUtils esUtils;
    @Override
    public List<Comment> selectListByFilmId(Integer film_id) {
        //构建筛选条件（影片的id）
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.should(QueryBuilders.termQuery("film_id",film_id));
        //把筛选条件存到搜索条件中
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(query);
        //执行ES工具类中的search方法，从es中取数据
        ESResponse<Comment> esResponse = esUtils.search(
                "comment",         //要从哪个Index（数据结构）中查询
                builder,                 //传入搜索条件
                Comment.class            //从es中得到的查询结果要转换成什么类型
        );
        //返回
        return esResponse.getData();
    }
}
