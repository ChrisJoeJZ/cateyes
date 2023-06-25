package cateye.chris.service;

import cateye.chris.bean.po.Comment;

import java.util.List;

//评论的业务层接口类
public interface CommentService {
    //按照影片的id，查询电影的评论业务（每页显示十条，分页）
    List<Comment> selectListByFilmId(Integer film_id);
}
