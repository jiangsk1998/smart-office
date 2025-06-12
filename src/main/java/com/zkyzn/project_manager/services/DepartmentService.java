package com.zkyzn.project_manager.services;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.DepartmentDao;
import com.zkyzn.project_manager.models.Department;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织架构服务类
 */
@Service
public class DepartmentService extends MPJBaseServiceImpl<DepartmentDao, Department> {

    /**
     * 获取完整的组织架构树
     * @return 组织架构树形结构列表（只包含根节点，子节点在children属性中）
     */
    public List<Department> getDepartmentTree() {
        // 1. 从数据库中查询出所有部门
        List<Department> allDepartments = this.list();
        if (allDepartments == null || allDepartments.isEmpty()) {
            return List.of();
        }

        // 2. 将列表转换为Map，以部门ID为键，方便快速查找
        Map<Long, Department> departmentMap = allDepartments.stream()
                .collect(Collectors.toMap(Department::getId, department -> department));

        // 3. 遍历所有部门，构建父子关系
        allDepartments.forEach(department -> {
            // 获取当前部门的上级部门ID
            Long parentId = department.getParentId();
            if (parentId != null) {
                // 从Map中找到父部门
                Department parent = departmentMap.get(parentId);
                if (parent != null) {
                    // 如果父部门的children列表还未初始化，则初始化
                    if (parent.getChildren() == null) {
                        parent.setChildren(new java.util.ArrayList<>());
                    }
                    // 将当前部门添加到父部门的children列表中
                    parent.getChildren().add(department);
                }
            }
        });

        // 4. 筛选出所有根节点（没有上级部门的节点），并返回
        return allDepartments.stream()
                .filter(department -> department.getParentId() == null)
                .collect(Collectors.toList());
    }
}