package com.xxx.blog.controller;

import com.xxx.blog.dao.pojo.SysUser;
import com.xxx.blog.utils.UserThreadLocal;
import com.xxx.blog.vo.params.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//使用功能json进行交互
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        return Result.success(null);
    }
}
