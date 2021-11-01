package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Role;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface RoleMapper  extends BaseMapper<Role,Integer> {
    // 查询角色列表
    @MapKey("id")
    public List<Map<String,Object>> queryAllRoles();

}