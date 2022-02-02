package com.xxx.blog.controller;

import com.xxx.blog.service.SysUserLoginService;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController//使用功能json进行交互
@RequestMapping("users")
public class UserController {


    @Autowired
    private SysUserLoginService sysUserLoginService;

    @GetMapping("currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token){

        return sysUserLoginService.getUserInfoByToken(token);
    }

}
