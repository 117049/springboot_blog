package com.xxx.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.xxx.blog.dao.pojo.SysUser;
import com.xxx.blog.service.LoginService;
import com.xxx.blog.service.SysUserService;
import com.xxx.blog.utils.JWTUtils;
import com.xxx.blog.utils.UserThreadLocal;
import com.xxx.blog.vo.ErrorCode;
import com.xxx.blog.vo.params.LoginParam;
import com.xxx.blog.vo.params.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    private static final String salt = "xhs";

    @Autowired
    private SysUserService sysuserservice;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @Description: 检查参数是否合法，根据用户name和密码去表中查询是否存在，如果不存在，登录失败，
     * @Param: 如果存在使用jwt 生成token 返回给前端 token放入redis中
     * @Return: 登录认证的时候 先认证token是否合法，去redis认证是否存在
     * @Author: xhs
     * @Date: 2022/1/30
     */
    @Override
    public Result login(LoginParam loginParam) {
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();

        if(StringUtils.isEmpty(account)||StringUtils.isEmpty(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        // 使用md5进行加密有可能会被破解，加入加密盐
        password = DigestUtils.md5Hex(password + salt);

        SysUser sysuser = sysuserservice.findUser(account, password);
        if(sysuser==null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }

        String token = JWTUtils.createToken(sysuser.getId());


        //放入redis
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysuser), 1, TimeUnit.DAYS);

        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {

        if(!StringUtils.hasLength(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if(stringObjectMap==null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(!StringUtils.hasLength(userJson)){
            return null;
        }
        //将json解析为User对象
        SysUser sysUser_1 = JSON.parseObject(userJson, SysUser.class);

        return sysUser_1;
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }

    /**
     * @Description: 判断参数是否合法，判断账户是否存在，返回账户已经被注册，
     * 如果不存在，注册用户，生成token并返回，加上事务，一旦出现问题就回滚
     */
    @Override
    public Result register(LoginParam loginParam) {
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if (!StringUtils.hasLength(account)
                || !StringUtils.hasLength(password)
                || !StringUtils.hasLength(nickname)
        ){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }

        SysUser sysuser = sysuserservice.findUserByAccount(account);
        if(sysuser != null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }

        sysuser = new SysUser();
        sysuser.setNickname(nickname);
        sysuser.setAccount(account);
        sysuser.setPassword(DigestUtils.md5Hex(password+salt));
        sysuser.setCreateDate(System.currentTimeMillis());
        sysuser.setLastLogin(System.currentTimeMillis());
        sysuser.setAvatar("/static/img/logo.b3a48c0.png");
        sysuser.setAdmin(1); //1 为true
        sysuser.setDeleted(0); // 0 为false
        sysuser.setSalt("");
        sysuser.setStatus("");
        sysuser.setEmail("");
        this.sysuserservice.save(sysuser);

        //token
        String token = JWTUtils.createToken(sysuser.getId());

        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysuser),1, TimeUnit.SECONDS);
        //清除ThreadLocal中的数据
        UserThreadLocal.remove();
        return Result.success(token);
    }
}
