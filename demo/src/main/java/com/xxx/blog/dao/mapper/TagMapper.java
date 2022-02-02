package com.xxx.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.blog.dao.pojo.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagMapper extends BaseMapper<Tag> {
    List<Tag> findTagByArticleId(Long articleId);

    //查询最热的标签
    List<Long> findhotsTagIds(int limit);

    List<Tag> findTagsByTagsIds(List<Long> tagIds);
}
