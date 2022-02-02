package com.xxx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.blog.dao.dos.Archives;
import com.xxx.blog.dao.mapper.ArticleBodyMapper;
import com.xxx.blog.dao.mapper.ArticleMapper;
import com.xxx.blog.dao.mapper.ArticleTagMapper;
import com.xxx.blog.dao.pojo.*;
import com.xxx.blog.service.*;
import com.xxx.blog.utils.UserThreadLocal;
import com.xxx.blog.vo.ArticleBodyVo;
import com.xxx.blog.vo.ArticleVo;
import com.xxx.blog.vo.params.ArticleParam;
import com.xxx.blog.vo.params.PageParams;
import com.xxx.blog.vo.params.Result;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleTagMapper articleTagMapper;
    /**
     * @Description: 分页查询article数据库表得到结果
     */

    @Override
    public Result listArticle(PageParams pageparams) {
        Page<Article> page = new Page<>(pageparams.getPage(), pageparams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(page,
                pageparams.getCategoryId(),
                pageparams.getTagId(),
                pageparams.getYear(),
                pageparams.getMonth());

        List<Article> records = articleIPage.getRecords();

        return Result.success(copyList(records, true, true));
    }
//    @Override
//    public Result listArticle(PageParams pageparams) {
//        System.out.println(articleMapper);
//
//        Page<Article> page = new Page<>(pageparams.getPage(), pageparams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//        if(pageparams.getCategoryId()!=null){
//            queryWrapper.eq(Article::getCategoryId, pageparams.getCategoryId());
//        }
//        List<Long> articleIdList = new ArrayList<Long>();
//        if(pageparams.getTagId()!=null){
//            LambdaQueryWrapper<ArticleTag> articleTagequeryWrapperTag = new LambdaQueryWrapper<>();
//            articleTagequeryWrapperTag.eq(ArticleTag::getTagId, pageparams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagequeryWrapperTag);
//            for(ArticleTag articleTag : articleTags){
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if(articleIdList.size()>0){
//                queryWrapper.in(Article::getId, articleIdList);
//            }
//        }
//
//        //是否置顶排序
//        queryWrapper.orderByDesc(Article::getWeight);
//        //根据时间进行排序
//        queryWrapper.orderByDesc(Article::getCreateDate);
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records = articlePage.getRecords();
//        //需要对数据库读取的数据进行格式转换
//        List<ArticleVo> articleVoList = copyList(records, true, true);
//        System.out.println(articleVoList);
//        return Result.success(articleVoList);
//    }

    //首页最热文章
    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit "+limit);
        //大概意思就是 select id, title from article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles, false, false));
    }

    //首页最新文章
    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit "+limit);
        //大概意思就是 select id, title from article order by CreateDate desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles, false, false));
    }
    //首页文章归纳
    @Override
    public Result listArchives() {
        List<Archives> articles = articleMapper.listArchives();
        return Result.success(articles);
    }
    @Autowired
    private ThreadService threadService;
    //查看文章详情
    @Override
    public Result findArticleById(Long articleId) {
        Article article = this.articleMapper.selectById(articleId);
        log.info(article.getId().toString());
        ArticleVo articleVo = copy(article, true, true, true, true);
        log.info(articleVo.getId().toString());

        //查看完文章，增加阅读数，有没有问题？在查看完文章之后，本应该直接返回数据，这个时候做了一个更新操作
        //更新是加写锁，会阻塞读操作，此时性能会比较低，更新耗时，一旦更新出现问题，不能影响查看文章的操作
        //使用线程池解决
        //可以把更新操作扔到线程池执行
        //和主线程就不相关了

        threadService.updateArticleViewCount(articleMapper, article);

        return Result.success(articleVo);
    }
    //文章发布，构建article对象
    @Override
    public Result publish(ArticleParam articleParam) {
        SysUser sysUser = UserThreadLocal.get();

        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setWeight(Article.Article_Common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        article.setCommentCounts(0);
        article.setBodyId(-1L);

        //插入之后会生成一个文章id
        this.articleMapper.insert(article);
        List<TagVo> tags = articleParam.getTags();

        if(tags!=null){
            for(TagVo tag : tags){
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        Map<String, String> map = new HashMap<>();
        map.put("id", article.getId().toString());

        return Result.success(map);
    }


    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for(Article record: records){
            articleVoList.add(copy(record, isTag, isAuthor, false, false));
        }

        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for(Article record: records){
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategory));
        }

        return articleVoList;
    }

    @Autowired
    private CategoryService categoryService;

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory){
        //将后端系统中的类变量类型与数据库中的类型进行绑定，防止类型不一样的问题，实现解耦合
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-mm-dd HH:mm"));
        //并不是所有的都需要作者和标签
        if(isTag){
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagById(articleId));
        }
        if(isAuthor){
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }

        if(isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if(isCategory){
            Long categoryId = article.getCategoryId();
            articleVo.setCategorys(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBodyById(Long bodyId) {

        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;

    }
}
