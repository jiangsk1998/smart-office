package com.zkyzn.project_manager.stories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.mappers.UserInfoDao; // 引入 UserInfoDao
import com.zkyzn.project_manager.models.Department; // 引入 Department 实体
import com.zkyzn.project_manager.models.Role; // 引入 Role 实体
import com.zkyzn.project_manager.models.UserInfo; // 引入 UserInfo 实体
import com.zkyzn.project_manager.services.DepartmentService;
import com.zkyzn.project_manager.services.UserInfoService;
import com.zkyzn.project_manager.services.UserRoleService; // 引入 UserRoleService
import com.zkyzn.project_manager.so.PageReq;
import com.zkyzn.project_manager.so.user.UserDetailResp; // 引入 UserDetailResp
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 封装用户相关的复杂业务场景
 */
@Service
public class UserStory {

    @Resource
    private UserInfoService userInfoService; // 使用 UserInfoService 进行单表查询

    @Resource
    private DepartmentService departmentService; // 注入 DepartmentService

    @Resource
    private UserRoleService userRoleService; // 用于获取用户的角色列表

    /**
     * 根据用户ID获取用户详情（分步单表查询实现，包含部门信息和角色信息）
     * @param userId 用户ID
     * @return 用户详情响应体或 null
     */
    public UserDetailResp getUserDetailById(Long userId) {
        // 第一步：获取 UserInfo 实体
        UserInfo userInfo = userInfoService.GetByUserId(userId);
        if (userInfo == null) {
            return null;
        }

        UserDetailResp userDetailResp = new UserDetailResp();
        // 将 userInfo 的字段复制到 userDetailResp
        userDetailResp.setUserAccount(userInfo.getUserAccount());
        userDetailResp.setUserId(userInfo.getUserId());
        userDetailResp.setUserName(userInfo.getUserName());
        userDetailResp.setCreateTime(userInfo.getCreateTime());
        userDetailResp.setUpdateTime(userInfo.getUpdateTime());

        // 第二步：获取部门信息
        if (userInfo.getDepartmentId() != null) {
            Department department = departmentService.getDepartmentById(userInfo.getDepartmentId());
            if (department != null) {
                userDetailResp.setDepartmentName(department.getName());
                userDetailResp.setDepartmentId(department.getId());
            }
        }

        // 第三步：获取用户角色信息
        List<Role> roles = userRoleService.getRolesByUserId(userId);
        userDetailResp.setRoles(roles);

        return userDetailResp;
    }

    /**
     * 分页查询用户列表（分步单表查询实现，包含部门信息和角色信息）
     * @param pageReq 分页请求
     * @param username 用户名 (模糊查询)
     * @param departmentId 部门ID (精确查询)
     * @return 用户详情分页结果
     */
    public Page<UserDetailResp> pageUserDetails(PageReq pageReq, String username, Long departmentId) {
        // 第一步：分页查询 UserInfo 列表
        Page<UserInfo> userInfoPage = new Page<>(pageReq.getCurrent(), pageReq.getSize());
        LambdaQueryWrapper<UserInfo> userInfoWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            userInfoWrapper.like(UserInfo::getUserName, username);
        }
        if (departmentId != null) {
            userInfoWrapper.eq(UserInfo::getDepartmentId, departmentId);
        }
        userInfoWrapper.orderByDesc(UserInfo::getCreateTime);

        userInfoPage = userInfoService.page(userInfoPage, userInfoWrapper); // 执行单表分页查询

        // 转换 UserInfo Page 为 UserDetailResp Page
        Page<UserDetailResp> userDetailRespPage = new Page<>(userInfoPage.getCurrent(), userInfoPage.getSize(), userInfoPage.getTotal());
        userDetailRespPage.setPages(userInfoPage.getPages());


        List<UserInfo> userInfos = userInfoPage.getRecords();
        if (userInfos.isEmpty()) {
            userDetailRespPage.setRecords(Collections.emptyList());
            return userDetailRespPage;
        }

        // 第二步：批量获取所有相关部门信息
        Set<Long> departmentIds = userInfos.stream()
                .map(UserInfo::getDepartmentId)
                .filter(deptId -> deptId != null) // 过滤掉部门ID为null的用户
                .collect(Collectors.toSet());

        Map<Long, String> departmentNameMap;
        if (!departmentIds.isEmpty()) {
            List<Department> departments = departmentService.listDepartmentsByIds(departmentIds);
            departmentNameMap = departments.stream()
                    .collect(Collectors.toMap(Department::getId, Department::getName));
        } else {
            departmentNameMap = Collections.emptyMap();
        }

        // 第三步：遍历每个用户，填充部门名称和角色信息
        List<UserDetailResp> userDetailResps = userInfos.stream()
                .map(userInfo -> {
                    UserDetailResp resp = new UserDetailResp();
                    // 复制 UserInfo 字段
                    resp.setUserId(userInfo.getUserId());
                    resp.setUserName(userInfo.getUserName());

                    resp.setDepartmentId(userInfo.getDepartmentId());
                    resp.setCreateTime(userInfo.getCreateTime());
                    resp.setUpdateTime(userInfo.getUpdateTime());

                    // 填充部门名称
                    if (userInfo.getDepartmentId() != null) {
                        resp.setDepartmentName(departmentNameMap.get(userInfo.getDepartmentId()));
                    }

                    // 填充角色信息（为每个用户调用一次 userRoleService.getRolesByUserId）
                    List<Role> roles = userRoleService.getRolesByUserId(userInfo.getUserId());
                    resp.setRoles(roles);

                    return resp;
                })
                .collect(Collectors.toList());

        userDetailRespPage.setRecords(userDetailResps);
        return userDetailRespPage;
    }

    // 可以在这里添加用户其他业务逻辑方法
}
