package com.xxx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.blog.dao.mapper.SysUserMapper;
import com.xxx.blog.dao.pojo.SysUser;
import com.xxx.blog.service.LoginService;
import com.xxx.blog.service.SysUserService;
import com.xxx.blog.vo.ErrorCode;
import com.xxx.blog.vo.LoginUserVo;
import com.xxx.blog.vo.UserVo;
import com.xxx.blog.vo.params.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper SysUserMapper;


    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = SysUserMapper.selectById(id);
        if(sysUser==null){
            sysUser = new SysUser();
            sysUser.setNickname("默认昵称");
        }
        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.eq(SysUser::getPassword, password);
        queryWrapper.select(SysUser::getAccount,SysUser::getId, SysUser::getAvatar, SysUser::getNickname);
        queryWrapper.last(" limit 1");


        return SysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getAccount, account);
        lambdaQueryWrapper.last("limit 1");
        return SysUserMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public void save(SysUser sysuser) {
        //保存id会自动生成，默认生成的id是分布式id，采用雪花算法，用户多了需要用到分表操作，id需要用分布式id
        SysUserMapper.insert(sysuser);
    }

    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = SysUserMapper.selectById(id);
        if(sysUser==null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("码神之路");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(sysUser, userVo);
        userVo.setId(String.valueOf(sysUser.getId()));
        return userVo;
    }

}
