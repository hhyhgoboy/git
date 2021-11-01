package com.yjxxt.crm.service;

import com.yjxxt.crm.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class RoleService {
    @Autowired(required = false)
    private RoleMapper roleMapper;
    /**
     * 查询所有角色信息
     */
    public List<Map<String,Object>> queryAllRoles(){
        return roleMapper.queryAllRoles();
    }
}
