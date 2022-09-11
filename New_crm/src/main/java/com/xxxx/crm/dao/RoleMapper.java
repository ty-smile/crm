package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {

    //查询所有角色
    public List<Map<String,Object>> queryAllRoles(Integer id);

    //根据名称查询数据
    Role queryRoleByRoleName(String roleName);
}