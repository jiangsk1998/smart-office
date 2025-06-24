package com.zkyzn.project_manager.controllers;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zkyzn.project_manager.models.UserInfo;
import com.zkyzn.project_manager.services.UserInfoService;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.utils.JwtUtil; // 引入 JwtUtil
import com.zkyzn.project_manager.utils.RSAUtil;
import com.zkyzn.project_manager.utils.ResUtil;
import com.zkyzn.project_manager.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 引入加密类
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "api/token", description = "用户权限部分")
@RequestMapping("api/token")
public class TokenController {

    @Resource
    private UserInfoService userInfoService;
    @Value("${security.password.private-key}")
    private String privateKeyStr;

    // 引入BCryptPasswordEncoder用于密码加密
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(summary = "用户注册", description = "创建新用户")
    @PostMapping("/register")
    public Result<UserInfo> registerUser(@RequestBody UserInfo userInfo) {
        // 检查账号是否已存在
        if (userInfoService.GetByUserAccount(userInfo.getUserAccount()) != null) {
            return ResUtil.fail("用户账号已存在");
        }

        // 解密密码
        String rawPassword = userInfo.getUserPassword();
        try {
            PrivateKey privateKey = RSAUtil.loadPrivateKey(privateKeyStr);
            rawPassword = RSAUtil.decrypt(rawPassword, privateKey);
        } catch (Exception e) {
            log.error("使用公私钥解密密码失败，请联系管理员！", e);
            return ResUtil.fail("使用公私钥解密密码失败，请联系管理员！");
        }

        // 密码加密存储
        userInfo.setUserPassword(passwordEncoder.encode(rawPassword));
        userInfo.setCreateTime(ZonedDateTime.now());
        userInfo.setUpdateTime(ZonedDateTime.now());

        userInfoService.save(userInfo);
        return ResUtil.ok(userInfo);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login( // 返回String类型，代表JWT Token
            @RequestParam String userAccount,
            @RequestParam String userPassword) {

        UserInfo user = userInfoService.GetByUserAccount(userAccount);
        if (user == null) {
            return ResUtil.fail("用户不存在");
        }

        // 解密密码
        String rawPassword = userPassword;
        try {
            PrivateKey privateKey = RSAUtil.loadPrivateKey(privateKeyStr);
            rawPassword = RSAUtil.decrypt(rawPassword, privateKey);
        } catch (Exception e) {
            log.error("使用公私钥解密密码失败，请联系管理员！", e);
            return ResUtil.fail("使用公私钥解密密码失败，请联系管理员！");
        }

        // 验证密码
        if (!passwordEncoder.matches(userPassword, rawPassword)) { // 使用passwordEncoder.matches进行密码比对
            return ResUtil.fail("密码错误");
        }

        // 登录成功，生成JWT Token并返回
        String token = JwtUtil.createToken(user.getUserId()); // 使用新的createToken方法
        return ResUtil.ok(token);
    }

    @Operation(summary = "用户登出", security = @SecurityRequirement(name = "auth"))
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId != null) {
            JwtUtil.invalidateToken(currentUserId); // 使Redis中的Token失效
            SecurityContextHolder.clearContext(); // 清除SecurityContext中的认证信息
            return ResUtil.ok(true);
        }
        return ResUtil.fail("未登录或获取用户ID失败");
    }

    @Operation(summary = "更新用户信息", security = @SecurityRequirement(name = "auth"))
    @PutMapping("/{userId}")
    public Result<UserInfo> updateUser(@RequestBody UserInfo userInfo,@PathVariable Long userId) {
        // 设置更新时间
        userInfo.setUpdateTime(ZonedDateTime.now());
        userInfo.setUserId(userId);

        // 如果密码被修改，也需要加密
        if (userInfo.getUserPassword() != null && !userInfo.getUserPassword().isEmpty()) {
            // 解密密码
            String rawPassword = userInfo.getUserPassword();
            try {
                PrivateKey privateKey = RSAUtil.loadPrivateKey(privateKeyStr);
                rawPassword = RSAUtil.decrypt(rawPassword, privateKey);
            } catch (Exception e) {
                log.error("使用公私钥解密密码失败，请联系管理员！", e);
                return ResUtil.fail("使用公私钥解密密码失败，请联系管理员！");
            }
            userInfo.setUserPassword(passwordEncoder.encode(rawPassword));
        }

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

        // 解密密码
        String rawPassword = newPassword;
        try {
            PrivateKey privateKey = RSAUtil.loadPrivateKey(privateKeyStr);
            rawPassword = RSAUtil.decrypt(rawPassword, privateKey);
        } catch (Exception e) {
            log.error("使用公私钥解密密码失败，请联系管理员！", e);
            return ResUtil.fail("使用公私钥解密密码失败，请联系管理员！");
        }

        user.setUserPassword(passwordEncoder.encode(rawPassword)); // 密码加密
        user.setUpdateTime(ZonedDateTime.now());
        return ResUtil.ok(userInfoService.updateById(user));
    }
    
    //获取所有用户信息
    @Operation(summary = "获取所有用户信息")
    @GetMapping("/all")
    public ResultList<UserInfo> getAllUsers(
    ) {
        List<UserInfo> list = userInfoService.list();
        return ResUtil.list(list);
    }
    
     @Deprecated
    @Operation(summary = "获取当前用户信息", security = @SecurityRequirement(name = "auth"))
    @GetMapping("/current")
    public Result<UserInfo> getCurrentAdminUser(
            @RequestParam(name = "user_id") Long userId
    ) {
        return ResUtil.ok(userInfoService.GetByUserId(userId));
    }
}