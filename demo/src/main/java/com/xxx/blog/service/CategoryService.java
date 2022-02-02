package com.xxx.blog.service;

import com.xxx.blog.vo.CategoryVo;
import com.xxx.blog.vo.params.Result;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categoryDetailById(Long id);
}
