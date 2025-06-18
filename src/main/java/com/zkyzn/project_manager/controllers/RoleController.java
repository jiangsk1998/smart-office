package com.zkyzn.project_manager.controllers;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.Role;
import com.zkyzn.project_manager.services.UserInfoService;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.role.RoleAssignReq;
import com.zkyzn.project_manager.stories.RoleStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理API接口
 */
@RestController
@Tag(name = "api/role", description = "角色管理")
@RequestMapping("/api/role")
public class RoleController {

    @Resource
    private RoleStory roleStory;
    @Resource
    private UserInfoService userInfoService;

    @Operation(summary = "创建新角色")
    @PostMapping
    public Result<Boolean> createRole(@RequestBody @Valid Role role) {
        try {
            boolean success = roleStory.createRole(role);
            return ResUtil.ok(success);
        } catch (IllegalArgumentException e) {
            return ResUtil.fail(e.getMessage());
        }
    }

    @Operation(summary = "更新角色信息")
    @PutMapping("/{roleId}")
    public Result<Boolean> updateRole(@PathVariable Long roleId, @RequestBody @Valid Role role) {
        role.setRoleId(roleId);
        try {
            boolean success = roleStory.updateRole(role);
            return ResUtil.ok(success);
        } catch (IllegalArgumentException e) {
            return ResUtil.fail(e.getMessage());
        }
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{roleId}")
    public Result<Boolean> deleteRole(@PathVariable Long roleId) {
        boolean success = roleStory.deleteRole(roleId);
        return ResUtil.ok(success);
    }

    @Operation(summary = "根据ID获取角色详情")
    @GetMapping("/{roleId}")
    public Result<Role> getRoleById(@PathVariable Long roleId) {
        Role role = roleStory.getRoleById(roleId);
        if (role == null) {
            return ResUtil.fail("角色不存在");
        }
        return ResUtil.ok(role);
    }

    @Operation(summary = "分页查询角色列表")
    @GetMapping
    public ResultList<Role> pageRoles(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "role_name", required = false) String roleName,
            @RequestParam(value = "role_code", required = false) String roleCode
    ) {
        Page<Role> rolePage = roleStory.pageRoles((long) pageNum, (long) pageSize, roleName, roleCode);
        return ResUtil.list(rolePage);
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/all")
    public ResultList<Role> getAllRoles() {
        List<Role> roles = roleStory.getAllRoles();
        return ResUtil.list(roles);
    }

    @Operation(summary = "为用户分配角色")
    @PostMapping("/assign-to-user")
    public Result<Boolean> assignRolesToUser(@RequestBody @Valid RoleAssignReq req) {
        // 验证用户是否存在
        if (userInfoService.GetByUserId(req.getUserId()) == null) {
            return ResUtil.fail("用户不存在");
        }
        try {
            boolean success = roleStory.assignRolesToUser(req);
            return ResUtil.ok(success);
        } catch (IllegalArgumentException e) {
            return ResUtil.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取用户已分配的角色")
    @GetMapping("/user/{userId}")
    public ResultList<Role> getUserRoles(@PathVariable Long userId) {
        // 验证用户是否存在
        if (userInfoService.GetByUserId(userId) == null) {
            return new ResultList<>();
        }
        List<Role> roles = roleStory.getUserRoles(userId);
        return ResUtil.list(roles);
    }
}