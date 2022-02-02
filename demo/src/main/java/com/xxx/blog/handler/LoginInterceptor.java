package com.xxx.blog.handler;

import com.alibaba.fastjson.JSON;
import com.xxx.blog.dao.pojo.SysUser;
import com.xxx.blog.service.LoginService;
import com.xxx.blog.utils.UserThreadLocal;
import com.xxx.blog.vo.ErrorCode;
import com.xxx.blog.vo.params.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;


    //在执行Controller方法之前进行执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //判断请求的接口路径是否为HandlerMethod(Controller方法)
        //判断token是否为空，如果为空，未登录
        //如果不为空，进行登录验证
        //认证成功则放行,可能是springboot程序访问静态资源，此时要放行
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String token = request.getHeader("Authorization");
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");


        if(!StringUtils.hasLength(token)){
            Result fail = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(fail));
            return false;
        }

        SysUser sysUser = loginService.checkToken(token);
        if(sysUser==null){
            Result fail = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(fail));
            return false;
        }
        UserThreadLocal.put(sysUser);
        //登录成功，放行
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                         @Nullable Exception ex) throws Exception {
        UserThreadLocal.remove();
    }

}
