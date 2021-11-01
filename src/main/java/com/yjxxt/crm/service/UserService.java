package com.yjxxt.crm.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.PhoneUtil;
import com.yjxxt.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName, String userPwd) {
        checkLoginParam(userName, userPwd);
        //判断用户是否存在
        User temp = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(temp == null, "用户名不存在");
        //判断密码是否正确
        checkLoginPwd(userPwd, temp.getUserPwd());
        return builderUserInfo(temp);
    }

    /**
     * 构建返回目标对象
     *
     * @param user
     * @return
     */
    private UserModel builderUserInfo(User user) {
        //实例化目标对象
        UserModel userModel = new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 检验用户名和密码的参数
     *
     * @param userName
     * @param userPwd
     */
    private void checkLoginParam(String userName, String userPwd) {
        //判断用户是否存在
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        //判断密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "密码不能为空");
    }

    /**
     * 校验密码是否正确的方法
     *
     * @param userPwd
     * @param userPwd1
     */
    private void checkLoginPwd(String userPwd, String userPwd1) {
        //对输入的密码进行加密
        userPwd = Md5Util.encode(userPwd);
        //加密后的密码和数据库的进行对比
        AssertUtil.isTrue(!userPwd.equals(userPwd1), "用户密码不正确");

    }

    /**
     * 用户密码修改
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        //通过userId获取用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        //参数校验
        checkPasswordParms(user, oldPassword, newPassword, confirmPassword);
        //设置用户新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //执行更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户更新密码失败");
    }

    /**
     * 验证用户密码修改的参数
     *
     * @param user
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    private void checkPasswordParms(User user, String oldPassword, String newPassword, String confirmPassword) {
        //user对象非空验证
        AssertUtil.isTrue(user == null, "用户未登录或不存在");
        //原始密码非空验证
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "请输入原始密码！");
        //用户输入的老密码与数据库中加密的要保持一致
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))), "原始密码不正确");
        //新密码不为空
        AssertUtil.isTrue(StringUtils.isBlank(newPassword), "请输入新密码");
        //新密码不能与原始密码相同
        AssertUtil.isTrue(oldPassword.equals(newPassword), "新密码不能与原始密码相同");
        //确认密码非空验证
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword), "确认密码不能为空");
        //新密码要与确认密码相同
        AssertUtil.isTrue(!(newPassword.equals(confirmPassword)), "新密码与确认密码不一致");
    }
    /**
     * 查询所有的销售人员
     */
    public List<Map<String,Object>> queryAllSales(){
        return  userMapper.queryAllSales();
    }


    /**
     * 多条件查询用户数据
     */
    public  Map<String,Object> queryUserByParams(UserQuery query){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        PageInfo pageInfo = new PageInfo<>(userMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg","");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return  map;

    }
    /**
     * 添加用户
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public  void  saveUser(User user){
        //1.参数校验
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        //2.设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        //3.执行添加 判断结果
        AssertUtil.isTrue(userMapper.insertSelective(user)==null,"用户添加失败！");

    }

    /**
     * 参数校验方法
     * @param userName
     * @param email
     * @param phone
     */
    private void checkParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        //验证用户名是否存在
        User temp = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(temp!=null,"该用户已存在");
        AssertUtil.isTrue(StringUtils.isBlank(email),"请输入邮箱地址");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号格式不正确！");
    }

    /**
     * 更新用户
     */
    public  void  updateUser(User user){
//        1.参数校验
        //通过id查询用户对象
        User temp = userMapper.selectByPrimaryKey(user.getId());
        //判断对象是否存在
        AssertUtil.isTrue(temp==null,"待更新记录不存在！");
        //验证参数
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        //2.设置默认参数
        temp.setUpdateDate(new Date());
        //3.执行更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"用户更新失败");

    }

    /**
     * 删除用户
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public  void  deleteUserByIds(Integer[] ids){
    AssertUtil.isTrue(ids==null||ids.length==0,"请选择待删除的用户！");
    AssertUtil.isTrue(deleteBatch(ids)!=ids.length,"用户记录删除失败");

    }
}
