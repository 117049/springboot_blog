package com.xxx.blog.controller;

import com.xxx.blog.service.TagService;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  //使用功能json进行交互
@RequestMapping("tags")
public class TagesController {
    @Autowired
    private TagService tagService;

    @GetMapping("hot")
    public Result hot(){
        int limit = 6;
        return tagService.hots(limit);
    }

    @GetMapping
    public Result findAll(){
        return tagService.findAll();
    }

    @GetMapping("/detail")
    public Result findAllDetail(){
        return tagService.findAllDetail();
    }

    @GetMapping("/detail/{id}")
    public Result findAllDetailById(@PathVariable("id") Long id){
        return tagService.findAllDetailById(id);
    }

}
