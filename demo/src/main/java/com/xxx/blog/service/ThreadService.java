package com.xxx.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xxx.blog.config.SetUpQueue;
import com.xxx.blog.dao.mapper.ArticleMapper;
import com.xxx.blog.dao.pojo.Article;
import com.xxx.blog.vo.params.QueueResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {

    @Autowired
    SetUpQueue setupqueue;

    //期望在线程池执行，不会影响主线程
    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articlemapper, Article article) {

        //对article进行封装
        QueueResult queueResult = new QueueResult(1, "通过文章主体更新文章浏览量", article);
        setupqueue.QueueAdd(queueResult);

        try {
            Thread.sleep(5000);
            System.out.println("更新完成了。。。。。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Async("taskExecutor")
    public void updateArticleViewCountByArticleId(Long ArticleId) {

        //对article进行封装
        QueueResult queueResult = new QueueResult(2, "通过文章Id更新文章浏览量", ArticleId);
        setupqueue.QueueAdd(queueResult);

    }

}
