package com.zkyzn.project_manager.mappers;

import com.github.yulichang.base.MPJBaseMapper;
import com.zkyzn.project_manager.models.Department;
import org.springframework.stereotype.Repository;

/**
 * 组织架构表数据访问接口
 */
@Repository
public interface DepartmentDao extends MPJBaseMapper<Department> {
}