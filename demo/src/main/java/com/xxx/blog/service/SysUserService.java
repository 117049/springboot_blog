package com.xxx.blog.service;

import com.xxx.blog.dao.pojo.SysUser;
import com.xxx.blog.vo.UserVo;
import com.xxx.blog.vo.params.Result;


public interface SysUserService{
    SysUser findUserById(Long id);

    SysUser findUser(String account, String password);


    SysUser findUserByAccount(String account);

    void save(SysUser sysuser);
    UserVo findUserVoById(Long id);
}
