package com.yjxxt.crm.interceptors;

import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非法访问拦截
 */

public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    //获取cookie中的用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //判断id是否不为空，且数据库中存在对应的用户记录
        if (userId==null||userService.selectByPrimaryKey(userId)==null){
        throw   new NoLoginException("未登录");

        }
        //放行
        return  true;
    }
}
