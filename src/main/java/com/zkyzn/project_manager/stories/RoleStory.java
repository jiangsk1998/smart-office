package com.zkyzn.project_manager.stories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 引入 LambdaQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.Role;
import com.zkyzn.project_manager.services.RoleService;
import com.zkyzn.project_manager.services.UserRoleService;
import com.zkyzn.project_manager.so.role.RoleAssignReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors; // 确保引入 Collectors

/**
 * 封装角色相关的复杂业务场景
 */
@Service
public class RoleStory {

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    /**
     * 创建角色
     * @param role 角色实体
     * @return true 成功，false 失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(Role role) {
        if (roleService.existsByRoleCode(role.getRoleCode())) {
            throw new IllegalArgumentException("角色编码已存在: " + role.getRoleCode());
        }
        role.setCreateTime(ZonedDateTime.now());
        role.setUpdateTime(ZonedDateTime.now());
        return roleService.save(role);
    }

    /**
     * 更新角色
     * @param role 角色实体
     * @return true 成功，false 失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        Role existingRole = roleService.getById(role.getRoleId());
        if (existingRole == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        // 检查角色编码是否重复，如果更改了编码且新编码已存在
        if (!existingRole.getRoleCode().equals(role.getRoleCode()) && roleService.existsByRoleCode(role.getRoleCode())) {
            throw new IllegalArgumentException("角色编码已存在: " + role.getRoleCode());
        }
        role.setUpdateTime(ZonedDateTime.now());
        return roleService.updateById(role);
    }

    /**
     * 删除角色
     * @param roleId 角色ID
     * @return true 成功，false 失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        // TODO: 考虑删除角色时，是否需要解绑所有关联的用户，或者检查是否存在关联用户
        return roleService.removeById(roleId);
    }

    /**
     * 根据ID获取角色
     * @param roleId 角色ID
     * @return 角色实体或 null
     */
    public Role getRoleById(Long roleId) {
        return roleService.getById(roleId);
    }

    /**
     * 分页查询角色列表
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param roleName 角色名称 (模糊查询)
     * @param roleCode 角色编码 (精确查询)
     * @return 角色分页结果
     */
    public Page<Role> pageRoles(Long pageNum, Long pageSize, String roleName, String roleCode) {
        return roleService.pageRoles(pageNum, pageSize, roleName, roleCode);
    }

    /**
     * 获取所有角色
     * @return 所有角色列表
     */
    public List<Role> getAllRoles() {
        return roleService.list();
    }

    /**
     * 为用户分配角色
     * @param req 角色分配请求
     * @return true 成功，false 失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(RoleAssignReq req) {
        Long userId = req.getUserId();
        Set<Long> roleIds = req.getRoleIds();

        // 验证传入的角色ID是否存在（可选，但推荐在实际应用中添加）
        if (roleIds != null && !roleIds.isEmpty()) {
            // 使用 LambdaQueryWrapper 和 count() 结合 in 条件来判断存在的角色数量
            LambdaQueryWrapper<Role> roleExistWrapper = new LambdaQueryWrapper<>();
            roleExistWrapper.in(Role::getRoleId, roleIds); // 查询 roleIds 中存在的角色数量
            long existingRoleCount = roleService.count(roleExistWrapper); // 调用 roleService 的 count 方法

            if (existingRoleCount != roleIds.size()) {
                throw new IllegalArgumentException("部分角色ID不存在");
            }
        }

        return userRoleService.assignRolesToUser(userId, roleIds);
    }

    /**
     * 获取用户已分配的角色列表
     * @param userId 用户ID
     * @return 该用户拥有的角色列表
     */
    public List<Role> getUserRoles(Long userId) {
        return userRoleService.getRolesByUserId(userId);
    }
}