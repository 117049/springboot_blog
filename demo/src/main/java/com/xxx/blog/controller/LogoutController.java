package com.xxx.blog.controller;

import com.xxx.blog.service.LoginService;
import com.xxx.blog.service.SysUserLoginService;
import com.xxx.blog.vo.params.LoginParam;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("logout")
public class LogoutController {

    @Autowired
    private LoginService LoginService;

    @GetMapping
    public Result currentUser(@RequestHeader("Authorization") String token){

        return LoginService.logout(token);
    }
}