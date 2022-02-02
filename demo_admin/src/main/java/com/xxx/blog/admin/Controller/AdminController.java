package com.xxx.blog.admin.Controller;

import com.xxx.blog.admin.Pojo.Permission;
import com.xxx.blog.admin.Service.PermissionService;
import com.xxx.blog.admin.Vo.Result;
import com.xxx.blog.admin.model.params.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("permission/permissionList")
    public Result ListPermission(@RequestBody PageParam pageParam){
        return permissionService.ListPermission(pageParam);
    }

    @PostMapping("permission/add")
    public Result add(@RequestBody Permission permission){
        return permissionService.add(permission);
    }

    @PostMapping("permission/update")
    public Result update(@RequestBody Permission permission){
        return permissionService.update(permission);
    }

    @GetMapping("permission/delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        return permissionService.delete(id);
    }

}
