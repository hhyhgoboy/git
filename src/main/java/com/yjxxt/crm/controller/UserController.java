package com.yjxxt.crm.controller;

import com.alibaba.fastjson.JSON;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public ResultInfo loginIndex(User user) {
        ResultInfo resultInfo = new ResultInfo();
        //调用servcie层的登录方法  得到返回的用户对象
        UserModel userModel = userService.userLogin(user.getUserName(), user.getUserPwd());
        resultInfo.setResult(userModel);
        System.out.println(JSON.toJSON(resultInfo));
        return resultInfo;
    }

    /**
     * 用户密码修改
     *
     * @param request
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    @RequestMapping("updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request, String oldPassword, String newPassword, String confirmPassword) {
        ResultInfo resultInfo = new ResultInfo();
        //获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service层的密码修改方法
        userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);
        return resultInfo;
    }

    //跳转到修改密码页面
    @RequestMapping("toPwdPage")
    public String toPasswordPage() {
        return "user/password";
    }

    //跳转到用户资料页面，获取当前登陆用户信息
    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest req) {
        //通过工具类  获取用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //根据id 查询用户信息
        User user = userService.selectByPrimaryKey(userId);
        //存储到作用域中
        req.setAttribute("user", user);
        //将拿到的信息返回给页面上
        return "user/setting";
    }
    //查询所有销售人员
    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }

    // 多条件查询用户信息

    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryUserByParams(UserQuery userQuery) {
        return userService.queryUserByParams(userQuery);

        }
    //进入用户界面
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }
    //添加用户
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveUser(User user){
        userService.saveUser(user);
        return  success("用户添加成功");
    }
    //更新用户
    @RequestMapping("update")
    @ResponseBody
    public  ResultInfo updateUser(User user){
        userService.updateUser(user);
        return  success("用户更新成功！");

    }
    //进入用户添加或更新页面
    @RequestMapping("addOrUpdatePage")
    public  String addUserPage(Integer id, Model model){
        if (id!=null){
            model.addAttribute("user",userService.selectByPrimaryKey(id));
        }
        return  "user/add_update";

    }

    //删除用户
    public ResultInfo  deleteUser(Integer[] ids){
        userService.deleteBatch(ids);
        return  success("用户删除成功");
    }

}
