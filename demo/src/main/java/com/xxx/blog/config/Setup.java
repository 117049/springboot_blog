package com.xxx.blog.config;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xxx.blog.dao.mapper.ArticleMapper;
import com.xxx.blog.dao.pojo.Article;
import com.xxx.blog.vo.params.QueueResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class Setup implements SetUpQueue, CommandLineRunner {

    private static BlockingQueue queue = new LinkedBlockingQueue();

    @Autowired
    private ArticleMapper articlemapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void QueueAdd(QueueResult article) {
        queue.add(article);
    }


    @Override
    public void QueueConsume() {
        while(true){
            log.info("阻塞状态---------------------------");

            QueueResult poll = null;
            try {
                poll = (QueueResult) queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int catagoryId = poll.getCatagoryId();

            if(catagoryId == 1){
                log.info("队列更新完成");
                Article article = (Article) poll.getDataClass();
                int viewCounts = article.getViewCounts();

                Article ArticleUpdate = new Article();
                ArticleUpdate.setViewCounts(viewCounts+1);
                //update article set view_count=100 where view_count=99 and id=11;
                LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Article::getId, article.getId());

                //设置一个CAS，为了在多线程下使用
                updateWrapper.eq(Article::getViewCounts, viewCounts);
                int update = articlemapper.update(ArticleUpdate, updateWrapper);
                if(update == 0){
                    System.out.println("修改失败，重新添加队列对应节点");
                    queue.offer(poll);
                }
            }
            else if(catagoryId == 2){
                // 通过文章Id更新浏览量
                log.info("队列更新完成");
                Long ArticleId = (Long) poll.getDataClass();
                // 先查询对应文章的阅读量
                Article article = this.articlemapper.selectById(ArticleId);
                Article ArticleUpdate = new Article();
                Integer viewcount = article.getViewCounts();
                ArticleUpdate.setViewCounts(viewcount + 1);
                //update article set view_count=100 where view_count=99 and id=11;


                LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Article::getId, ArticleId);

                int update = articlemapper.update(ArticleUpdate, updateWrapper);
                if(update == 0){
                    System.out.println("修改失败，重新添加队列对应节点");
                    queue.offer(poll);
                }
            }else if(catagoryId == 3){
                log.info("队列更新完成");
                //删除最新文章缓存
                Boolean deletenew = redisTemplate.delete("news_article::ArticleController::newArticles::");
                //删除最热文章缓存
                Boolean deletehot = redisTemplate.delete("hot_article::ArticleController::hotArticles::");
                //删除列表缓存
                Set<String> keysdel = redisTemplate.keys("list_article::ArticleController::listArticles::" + "*");
                Long deletelist = redisTemplate.delete(keysdel);
            }
        }
    }

    @Override
    public int QueueSize() {
        return 0;
    }


    @Override
    public void run(String... args) throws Exception {

        Thread t1 = new Thread(){
            public void run(){
                QueueConsume();
            }
        };
        t1.setDaemon(true);
        t1.start();
    }
}
