package com.xxx.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xxx.blog.dao.mapper.ArticleMapper;
import com.xxx.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {
    //期望在线程池执行，不会影响主线程
    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articlemapper, Article article) {

        int viewCounts = article.getViewCounts();
        Article ArticleUpdate = new Article();
        ArticleUpdate.setViewCounts(viewCounts+1);
        //update article set view_count=100 where view_count=99 and id=11;
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, article.getId());
        //设置一个CAS，为了在多线程下使用
        updateWrapper.eq(Article::getViewCounts, viewCounts);
        articlemapper.update(ArticleUpdate, updateWrapper);

        try {
            Thread.sleep(5000);
            System.out.println("更新完成了。。。。。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
