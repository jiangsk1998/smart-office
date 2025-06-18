package com.zkyzn.project_manager.controllers;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zkyzn.project_manager.models.UserInfo;
import com.zkyzn.project_manager.services.UserInfoService;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@Tag(name = "api/token", description = "用户权限部分")
@RequestMapping("api/token")
public class TokenController {

    @Resource
    private UserInfoService userInfoService;


    @Deprecated
    @Operation(summary = "获取当前用户信息", security = @SecurityRequirement(name = "auth"))
    @GetMapping("/current")
    public Result<UserInfo> getCurrentAdminUser(
            @RequestParam(name = "user_id") Long userId
    ) {
        return ResUtil.ok(userInfoService.GetByUserId(userId));
    }


    //获取所有用户信息
    @Operation(summary = "获取所有用户信息")
    @GetMapping("/all")
    public ResultList<UserInfo> getAllUsers(
    ) {
        List<UserInfo> list = userInfoService.list();
        return ResUtil.list(list);
    }

    @Operation(summary = "用户注册", description = "创建新用户")
    @PostMapping("/register")
    public Result<UserInfo> registerUser(@RequestBody UserInfo userInfo) {
        // 检查账号是否已存在
        if (userInfoService.GetByUserAccount(userInfo.getUserAccount()) != null) {
            return ResUtil.fail("用户账号已存在");
        }

        // 设置创建时间
        userInfo.setCreateTime(ZonedDateTime.now());
        userInfo.setUpdateTime(ZonedDateTime.now());

        // 保存用户（密码需要前端加密后传输）
        userInfoService.save(userInfo);
        return ResUtil.ok(userInfo);
    }

    @Operation(summary = "更新用户信息", security = @SecurityRequirement(name = "auth"))
    @PutMapping("/{userId}")
    public Result<UserInfo> updateUser(@RequestBody UserInfo userInfo) {
        // 设置更新时间
        userInfo.setUpdateTime(ZonedDateTime.now());

        if (userInfoService.updateById(userInfo)) {
            return ResUtil.ok(userInfo);
        }
        return ResUtil.fail("更新失败");
    }

    @Operation(summary = "删除用户", security = @SecurityRequirement(name = "auth"))
    @DeleteMapping("/{userId}")
    public Result<Boolean> deleteUser(@PathVariable Long userId) {
        // 逻辑删除（@TableLogic 会自动处理）
        return ResUtil.ok(userInfoService.removeById(userId));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserInfo> login(
            @RequestParam String userAccount,
            @RequestParam String userPassword) {

        UserInfo user = userInfoService.GetByUserAccount(userAccount);
        if (user == null) {
            return ResUtil.fail("用户不存在");
        }

        // 实际项目中密码需加密比较
        if (!user.getUserPassword().equals(userPassword)) {
            return ResUtil.fail("密码错误");
        }

        // 返回用户信息（实际项目中应返回token）
        return ResUtil.ok(user);
    }

    @Deprecated
    @Operation(summary = "分页查询用户", security = @SecurityRequirement(name = "auth"))
    @GetMapping("/page")
    public Result<IPage<UserInfo>> getUserPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String userName) {

        IPage<UserInfo> userPage = userInfoService.page(current, size, userName);
        return ResUtil.ok(userPage);
    }

    @Deprecated
    @Operation(summary = "获取用户详情", security = @SecurityRequirement(name = "auth"))
    @GetMapping("/{userId}/detail")
    public Result<UserInfo> getUserDetail(@PathVariable Long userId) {
        UserInfo user = userInfoService.GetByUserId(userId);
        return user != null ? ResUtil.ok(user) : ResUtil.fail("用户不存在");
    }

    @Operation(summary = "重置密码", security = @SecurityRequirement(name = "auth"))
    @PutMapping("/{userId}/reset-password")
    public Result<Boolean> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword) {

        UserInfo user = userInfoService.GetByUserId(userId);
        if (user == null) {
            return ResUtil.fail("用户不存在");
        }

        user.setUserPassword(newPassword); // 实际项目中需加密
        user.setUpdateTime(ZonedDateTime.now());
        return ResUtil.ok(userInfoService.updateById(user));
    }
}
