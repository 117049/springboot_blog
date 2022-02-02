package com.xxx.blog.service.impl;

import com.xxx.blog.dao.pojo.SysUser;
import com.xxx.blog.service.LoginService;
import com.xxx.blog.service.SysUserLoginService;
import com.xxx.blog.vo.ErrorCode;
import com.xxx.blog.vo.LoginUserVo;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserLoginServiceImpl implements SysUserLoginService {

    @Autowired
    private LoginService loginService;

    /**
     * @Description: token合法性验证，是否成功，解析是否成功，redis是否存在，如果校验失败，返回false，如果成功返回LoginUserVo
     */
    @Override
    public Result getUserInfoByToken(String token) {

        SysUser sysUser = loginService.checkToken(token);

        if(sysUser==null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        LoginUserVo loginUser = new LoginUserVo();
        loginUser.setId(String.valueOf(sysUser.getId()));
        loginUser.setAccount(sysUser.getAccount());
        loginUser.setNickname(sysUser.getNickname());
        loginUser.setAvatar(sysUser.getAvatar());

        return Result.success(loginUser);
    }
}
