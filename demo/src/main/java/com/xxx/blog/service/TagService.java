package com.xxx.blog.service;

import com.xxx.blog.dao.pojo.TagVo;
import com.xxx.blog.vo.params.Result;

import java.util.List;

public interface TagService {
    List<TagVo> findTagById(Long articleId);

    Result hots(int limit);

    Result findAll();

    Result findAllDetail();

    Result findAllDetailById(Long id);
}
