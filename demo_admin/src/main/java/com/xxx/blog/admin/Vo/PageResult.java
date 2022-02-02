package com.xxx.blog.admin.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private Long total;
}
