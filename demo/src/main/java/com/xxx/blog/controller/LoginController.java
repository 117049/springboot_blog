package com.xxx.blog.controller;

import com.xxx.blog.service.LoginService;
import com.xxx.blog.utils.UserThreadLocal;
import com.xxx.blog.vo.params.LoginParam;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result login(@RequestBody LoginParam loginParam){
        Result login = loginService.login(loginParam);
        return login;
    }
}
