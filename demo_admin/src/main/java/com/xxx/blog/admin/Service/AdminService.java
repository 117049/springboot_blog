package com.xxx.blog.admin.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.blog.admin.Mapper.AdminMapper;
import com.xxx.blog.admin.Pojo.Admin;
import com.xxx.blog.admin.Pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;

    public Admin findAdminByUser(String userName){
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, userName);
        queryWrapper.last(" limit 1 ");
        Admin admin = adminMapper.selectOne(queryWrapper);
        return admin;
    }


    public List<Permission> findPermissionsByAdminId(Long id) {

        return adminMapper.findpermissionByAdminId(id);
    }
}
