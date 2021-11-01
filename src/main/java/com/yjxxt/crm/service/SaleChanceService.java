package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.mapper.SaleChanceMapper;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件分页查询营销机会(BaseService中有对应的方法)
     * @param query
     * @return
     */
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery query) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        PageInfo pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return  map;
    }

/**
 * 营销机会数据添加
 *
 */
public  void  saveSaleChance(SaleChance saleChance){
    //1.参数校验
    checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
    //2.设置相关参数默认值
    //state未分配 0  分配了 1      devResult  0未开发  1开发中  2 开发成功 3 开发失败
    if (StringUtils.isBlank(saleChance.getAssignMan())){
        saleChance.setState(0);
        saleChance.setDevResult(0);
    }
    //分配了 1
    if (StringUtils.isNotBlank(saleChance.getAssignMan())){
        saleChance.setState(1);
        saleChance.setDevResult(1);
        saleChance.setAssignTime(new Date());

    }
    //分配时间
    saleChance.setCreateDate(new Date());
    saleChance.setUpdateDate(new Date());
    saleChance.setIsValid(1);
    //添加是否成功
    AssertUtil.isTrue(insertSelective(saleChance)<1,"添加失败了");
}

    private void checkParams(String customerName, String linkMan, String linkPhone) {
    AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入客户名称");
    AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人");
    AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入手机号");
    AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机格式不正确");
    }
    /**
     *营销机会数据更新
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public  void  updateSaleChance(SaleChance saleChance){
        //1.参数校验
        //通过id查询记录
        SaleChance temp = selectByPrimaryKey(saleChance.getId());
        //判断就是否为空
        AssertUtil.isTrue(temp==null,"待更新记录不存在");
        //校验参数
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        // 2.设置相关参数值
        //原始记录 未分配 修改后改为已分配(由分配人决定)
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
        saleChance.setState(1);
        saleChance.setDevResult(1);
        saleChance.setAssignTime(new Date());
        }
        //原始记录已分配 修改后为未分配
        if(StringUtils.isNotBlank(temp.getAssignMan())&& StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //设置默认
        saleChance.setUpdateDate(new Date());
        //修改是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance)<1,"修改失败了");
    }
    /**
     * 营销机会删除
     */
    @Transactional(propagation =Propagation.REQUIRED)
    public  void  deleteSaleChance(Integer ids[]){
        //判断要删除的id是否为空
        AssertUtil.isTrue(ids==null||ids.length==0,"亲选择要删除的数据！");
        //删除数据
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)<0,"营销机会删除失败");
    }
}
