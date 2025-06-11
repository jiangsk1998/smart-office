package com.zkyzn.project_manager.controllers;

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

import java.util.List;

@RestController
@Tag(name = "api/token", description = "用户权限部分")
@RequestMapping("api/token")
public class TokenController {

    @Resource
    private UserInfoService userInfoService;


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
}
