package com.zkyzn.project_manager.mappers;

import com.github.yulichang.base.MPJBaseMapper;
import com.zkyzn.project_manager.models.UserRole;
import org.springframework.stereotype.Repository;

/**
 * 用户角色关系表数据访问接口
 */
@Repository
public interface UserRoleDao extends MPJBaseMapper<UserRole> {
}