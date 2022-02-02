package com.xxx.blog.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.xxx.blog.dao.pojo.TagVo;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ArticleVo {

    //保证雪花算法得到的id精度，copy方法的Long转型会掉精度
    //@JsonSerialize(using = ToStringSerializer.class)
    private String id;

    private String title;

    private String summary;

    private Integer commentCounts;

    private Integer viewCounts;

    private Integer weight;
    /**
     * 创建时间
     */
    private String createDate;

    private String author;

    private ArticleBodyVo body;

    private List<TagVo> tags;

    private CategoryVo categorys;

}

