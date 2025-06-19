package com.zkyzn.project_manager.controllers;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.UserInfo;
import com.zkyzn.project_manager.services.UserInfoService;
import com.zkyzn.project_manager.stories.UserStory; // 引入 UserStory
import com.zkyzn.project_manager.so.PageReq; // 引入 PageReq
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.user.UserDetailResp; // 引入 UserDetailResp
import com.zkyzn.project_manager.utils.ResUtil;
import com.zkyzn.project_manager.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息管理API接口
 */
@RestController
@Tag(name = "api/user-info", description = "用户信息管理")
@RequestMapping("/api/user-info")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserStory userStory;



    @Operation(summary = "根据ID获取用户详情（包含部门和角色信息）")
    @GetMapping("/{userId}/detail")
    public Result<UserDetailResp> getUserDetail(@PathVariable Long userId) {
        UserDetailResp userDetail = userStory.getUserDetailById(userId);
        if (userDetail == null) {
            return ResUtil.fail("用户不存在");
        }
        return ResUtil.ok(userDetail);
    }

    @Operation(summary = "获取当前用户详情（包含部门和角色信息）")
    @GetMapping("/currentUser/detail")
    public Result<UserDetailResp> geCurrentUserDetail() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResUtil.fail("用户未登录"); // 如果无法获取当前用户ID，表示未登录
        }
        UserDetailResp userDetail = userStory.getUserDetailById(currentUserId);
        if (userDetail == null) {
            return ResUtil.fail("用户不存在"); // 理论上不会发生，因为ID是从已认证用户获取的
        }
        return ResUtil.ok(userDetail);
    }

    @Operation(summary = "分页查询用户列表（包含部门和角色信息）")
    @GetMapping("/details")
    public ResultList<UserDetailResp> pageUsers(
            @Valid PageReq pageReq,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "department_id", required = false) Long departmentId
    ) {
        Page<UserDetailResp> userPage = userStory.pageUserDetails(pageReq, username, departmentId);
        return ResUtil.list(userPage);
    }

//     @Operation(summary = "创建用户")
//     @PostMapping
//     public Result<Boolean> createUser(@RequestBody @Valid UserInfo userInfo) {
//         boolean success = userInfoService.save(userInfo);
//         return ResUtil.ok(success);
//     }
//
//     @Operation(summary = "更新用户")
//     @PutMapping("/{userId}")
//     public Result<Boolean> updateUser(@PathVariable Long userId, @RequestBody @Valid UserInfo userInfo) {
//         userInfo.setUserId(userId);
//         boolean success = userInfoService.updateById(userInfo);
//         return ResUtil.ok(success);
//     }
}