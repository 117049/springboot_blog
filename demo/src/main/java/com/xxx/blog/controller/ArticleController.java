package com.xxx.blog.controller;


import com.xxx.blog.common.aop.LogAnnotation;
import com.xxx.blog.common.cache.Cache;
import com.xxx.blog.common.cache.CacheAll;
import com.xxx.blog.service.ArticleService;
import com.xxx.blog.vo.params.ArticleParam;
import com.xxx.blog.vo.params.PageParams;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleservice;

    @PostMapping
    @LogAnnotation(model="文章", operater="获取文章列表")
    @Cache(expire = 5*60*1000, name="list_article")
    public Result listArticles(@RequestBody PageParams pageParams){

        System.out.println(articleservice.listArticle(pageParams));
        return articleservice.listArticle(pageParams);

    }
    //最热文章
    @PostMapping("/hot")
    @Cache(expire = 5*60*1000, name="hot_article")
    public Result hotArticles(){
        int limit = 6;
        return articleservice.hotArticle(limit);
    }
    //最新文章
    @PostMapping("/new")
    @Cache(expire = 5*60*1000, name="news_article")
    public Result newArticles(){
        int limit = 5;
        return articleservice.newArticle(limit);
    }
    //文章归纳
    @PostMapping("/listArchives")
    public Result newlistArchives(){
        return articleservice.listArchives();
    }

    //查看文章详情
    @PostMapping("view/{id}")
    @CacheAll(name="all_article")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleservice.findArticleById(articleId);
    }

    //发布文章
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleservice.publish(articleParam);
    }
}
