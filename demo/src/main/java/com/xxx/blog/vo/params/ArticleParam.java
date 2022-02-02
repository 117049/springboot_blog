package com.xxx.blog.vo.params;

import com.xxx.blog.dao.pojo.TagVo;
import com.xxx.blog.vo.CategoryVo;
import lombok.Data;

import java.util.List;

@Data
public class ArticleParam {

    private Long id;

    private ArticleBodyParam body;

    private CategoryVo category;

    private String summary;

    private List<TagVo> tags;

    private String title;
}