package com.xxx.blog.service;

import com.xxx.blog.vo.params.ArticleParam;
import com.xxx.blog.vo.params.PageParams;
import com.xxx.blog.vo.params.Result;

public interface ArticleService {
    /**
     * @Description: 分页查询文章列表
     * @Param:
     * @Return:
     * @Author: xhs
     * @Date: 2022/1/26
     */
    Result listArticle(PageParams pageParams);

    //首页最热文章
    Result hotArticle(int limit);
    //首页最新文章
    Result newArticle(int limit);
    //首页文章归纳
    Result listArchives();
    //查看文章详情
    Result findArticleById(Long articleId);
    //文章发布
    Result publish(ArticleParam articleParam);
}
