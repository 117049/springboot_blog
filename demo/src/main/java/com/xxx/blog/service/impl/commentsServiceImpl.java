package com.xxx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.blog.dao.mapper.CommentMapper;
import com.xxx.blog.dao.pojo.Comment;
import com.xxx.blog.dao.pojo.SysUser;
import com.xxx.blog.service.SysUserService;
import com.xxx.blog.service.commentsService;
import com.xxx.blog.utils.UserThreadLocal;
import com.xxx.blog.vo.CommentVo;
import com.xxx.blog.vo.UserVo;
import com.xxx.blog.vo.params.CommentParam;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class commentsServiceImpl implements commentsService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public Result commentsByArticleId(Long id) {
        //根据文章id，查询评论，从comment表中查
        //根据作者的id,查询作者的信息
        //判断如果level=1 要去查询它有没有子评论
        //如果有根据评论id查询
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Comment::getArticleId, id);
        queryWrapper.eq(Comment::getLevel, 1);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList = copyList(comments);

        return Result.success(commentVoList);
    }
    //添加评论
    @Override
    public Result comment(CommentParam commentParam) {
        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParam.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParam.getParent();
        if (parent == null || parent == 0) {
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        comment.setParentId(parent == null ? 0 : parent);
        Long toUserId = commentParam.getToUserId();
        comment.setToUid(toUserId == null ? 0 : toUserId);
        this.commentMapper.insert(comment);

        return Result.success(null);
    }

    private List<CommentVo> copyList(List<Comment> comments) {
        List<CommentVo> commentVoList = new ArrayList<CommentVo>();
        for(Comment comment : comments){
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        commentVo.setId(String.valueOf(comment.getId()));
        //作者id
        Long authorId = comment.getAuthorId();

        UserVo userVo = sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);
        //子评论
        Integer level = comment.getLevel();
        if(level==1){
            Long id = comment.getId();
            List<CommentVo> commentVoList = findCommentsByParentsId(id);
            commentVo.setChildrens(commentVoList);
        }

        //评论的评论是给谁的
        if(level>1){
            Long toUid = comment.getToUid();
            UserVo userVoById = sysUserService.findUserVoById(toUid);
            commentVo.setToUser(userVoById);
        }

        return commentVo;
    }

    private List<CommentVo> findCommentsByParentsId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, id);
        queryWrapper.eq(Comment::getLevel, 2);
        return copyList(commentMapper.selectList(queryWrapper));
    }

}
