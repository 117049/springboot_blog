package com.xxx.blog.service;

import com.xxx.blog.vo.params.CommentParam;
import com.xxx.blog.vo.params.Result;


public interface commentsService {
    Result commentsByArticleId(Long id);

    Result comment(CommentParam commentParam);
}
