package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl; // 确认引入这个类
import com.zkyzn.project_manager.mappers.UserRoleDao;
import com.zkyzn.project_manager.models.Role;
import com.zkyzn.project_manager.models.UserRole;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户角色关系业务逻辑服务类
 */
@Service
// 确保继承 MPJBaseServiceImpl
public class UserRoleService extends MPJBaseServiceImpl<UserRoleDao, UserRole> {

    @Resource
    private RoleService roleService;

    /**
     * 根据用户ID获取其所有角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<Role> getRolesByUserId(Long userId) {
        // 第一步：从 tab_user_role 表中，根据 userId 查询所有关联的 roleId
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        userRoleWrapper.select(UserRole::getRoleId);

        List<UserRole> userRoles = list(userRoleWrapper);

        // 提取所有 roleId
        Set<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        // 如果没有关联角色，则直接返回空列表
        if (roleIds.isEmpty()) {
            return List.of();
        }

        List<Role> roles = roleService.listByIds(roleIds);

        return roles;
    }

    /**
     * 批量分配用户角色
     *
     * @param userId  用户ID
     * @param roleIds 要分配的角色ID列表
     * @return true if successful, false otherwise
     */
    public boolean assignRolesToUser(Long userId, Set<Long> roleIds) {
        // 先删除该用户已有的所有角色
        LambdaQueryWrapper<UserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRole::getUserId, userId);
        remove(deleteWrapper);

        // 如果 roleIds 为空，则表示清空角色，直接返回 true
        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }

        // 批量插入新的角色关系
        List<UserRole> userRoles = roleIds.stream()
                .map(roleId -> {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .collect(Collectors.toList());

        return saveBatch(userRoles);
    }
}