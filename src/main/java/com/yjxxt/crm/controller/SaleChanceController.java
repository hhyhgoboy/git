package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 多条件分页查询营销机会
 */
@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Autowired
    private UserService userService;

    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParms(SaleChanceQuery query) {
        return saleChanceService.querySaleChanceByParams(query);

    }


    /**
     * 进入营销机会页面
     */
    @RequestMapping("index")
    public String index() {
        return "saleChance/sale_chance";
    }

    /**
     * 添加机会数据的 视图转发
     */

    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdateChancePage(Integer id, Model model) {
        //判断
        if (id != null) {
            //查询用户信息
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //存储
            model.addAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 添加营销机会数据
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveSaleChance(HttpServletRequest req, SaleChance saleChance) {
        //获取用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //获取用户的真是姓名
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //设置营销机会的创建人
        saleChance.setCreateMan(trueName);
        //添加营销机会的数据
        saleChanceService.saveSaleChance(saleChance);
        return success("营销机会数据添加成功");
    }

    /**
     * 修改营销数据
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(HttpServletRequest req, SaleChance saleChance) {
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功");
    }
/**
 * 删除营销机会数据
 */
@RequestMapping("delete")
@ResponseBody
public  ResultInfo  deleteSaleChance(Integer[] ids){
    //删除营销机会的数据
    saleChanceService.deleteSaleChance(ids);
    return success("营销机会删除成功");
}
}
