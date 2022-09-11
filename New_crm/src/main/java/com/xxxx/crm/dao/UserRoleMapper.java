package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {
      // <!--查询某个用户下的角色-->
    public Integer countUserRole(Integer uId);

  // <!--查询某个用户下的角色-->
    public Integer deleteUserRole(Integer uId);
}