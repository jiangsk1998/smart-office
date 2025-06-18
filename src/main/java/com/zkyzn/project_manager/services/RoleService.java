package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.RoleDao;
import com.zkyzn.project_manager.models.Role;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色业务逻辑服务类
 */
@Service
public class RoleService extends MPJBaseServiceImpl<RoleDao, Role> {

    /**
     * 根据角色编码查询角色
     * @param roleCode 角色编码
     * @return 角色实体或 null
     */
    public Role getByRoleCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return null;
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        return getOne(wrapper);
    }

    /**
     * 检查角色编码是否存在
     * @param roleCode 角色编码
     * @return 存在返回 true，否则返回 false
     */
    public boolean existsByRoleCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return false;
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        return count(wrapper) > 0;
    }

    /**
     * 分页查询角色信息
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param roleName 角色名称 (模糊查询)
     * @param roleCode 角色编码 (精确查询)
     * @return 分页结果
     */
    public Page<Role> pageRoles(Long pageNum, Long pageSize, String roleName, String roleCode) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        if (StringUtils.hasText(roleCode)) {
            wrapper.eq(Role::getRoleCode, roleCode);
        }
        wrapper.orderByDesc(Role::getCreateTime);

        return page(page, wrapper);
    }
}