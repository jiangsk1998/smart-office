package com.zkyzn.project_manager.mappers;

import com.github.yulichang.base.MPJBaseMapper;
import com.zkyzn.project_manager.models.Role;
import org.springframework.stereotype.Repository;

/**
 * 角色表数据访问接口
 */
@Repository
public interface RoleDao extends MPJBaseMapper<Role> {
}