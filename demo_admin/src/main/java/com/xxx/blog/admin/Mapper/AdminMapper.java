package com.xxx.blog.admin.Mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.blog.admin.Pojo.Admin;
import com.xxx.blog.admin.Pojo.Permission;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMapper extends BaseMapper<Admin> {

    @Select("SELECT * FROM ms_permission WHERE id IN (SELECT permission_id FROM ms_admin_permission WHERE admin_id = #{adminId})")
    List<Permission> findpermissionByAdminId(Long adminId);
}
