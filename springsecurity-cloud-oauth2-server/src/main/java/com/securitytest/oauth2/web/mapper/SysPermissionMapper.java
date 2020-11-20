package com.securitytest.oauth2.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.securitytest.oauth2.web.entities.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {
	
	List<SysPermission> selectPermissionByUserId(@Param("userId") Long userId);
	
	List<SysPermission> findByRoleId(@Param("roleId") Long roleId);
}
