package com.yjxxt.crm.controller;

import com.alibaba.fastjson.JSON;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

@Controller
public class IndexController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 系统登陆页面
     *
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "index";
    }

    /**
     * 系统界面欢迎页
     *
     * @return
     */
    @RequestMapping("welcome")
    public String welcome() {
        return "welcome";
    }

    /**
     * 后端管理主页面
     *
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request) {
        //通过工具类从cookie中获取userID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        System.out.println(userId);
        //调用对应service层的方法  通过userId主键查询用户对象
        User user = userService.selectByPrimaryKey(userId);
//        System.out.println(JSON.toJSON(user));
        request.setAttribute("user", user);
        return "main";
    }
}
