package com.xxx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xxx.blog.dao.mapper.TagMapper;
import com.xxx.blog.dao.pojo.Tag;
import com.xxx.blog.dao.pojo.TagVo;
import com.xxx.blog.service.TagService;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {


    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TagVo> findTagById(Long ArticleId) {
        List<Tag> tags = tagMapper.findTagByArticleId(ArticleId);
        return copyList(tags);
    }

    //最热标签，标签所拥有的文章数量最多，查询，根据tag_id进行分组计数，从大到小排序，取出前几个即可
    @Override
    public Result hots(int limit) {
        List<Long> tarIds = tagMapper.findhotsTagIds(limit);
        if(CollectionUtils.isEmpty(tarIds)){
            return Result.success(Collections.emptyList());
        }
        //根据target_id查询到tag_name
        //select * from tag where id in (1,2)
        List<Tag> tagList = tagMapper.findTagsByTagsIds(tarIds);
        return Result.success(tagList);
    }

    //查询所有的文章标签
    @Override
    public Result findAll() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId, Tag::getTagName);
        List<Tag> tags = this.tagMapper.selectList(queryWrapper);
        return Result.success(copyList(tags));
    }
    //查询所有的文章标签详细信息
    @Override
    public Result findAllDetail() {
        List<Tag> tags = this.tagMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(copyList(tags));
    }

    @Override
    public Result findAllDetailById(Long id) {
        Tag tag = tagMapper.selectById(id);
        return Result.success(copy(tag));
    }

    public TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        tagVo.setId(String.valueOf(tag.getId()));
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }
}
