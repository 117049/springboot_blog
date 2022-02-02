package com.xxx.blog.service;

import com.xxx.blog.vo.params.Result;

public interface SysUserLoginService {
    //根据token查询用户信息
    Result getUserInfoByToken(String token);

}
