package com.xxx.blog.service;

import com.xxx.blog.dao.pojo.SysUser;
import com.xxx.blog.vo.params.LoginParam;
import com.xxx.blog.vo.params.Result;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface LoginService{
    //登录功能
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);
    //退出功能
    Result logout(String token);

    Result register(LoginParam loginParam);
}
