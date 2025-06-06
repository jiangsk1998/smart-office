package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "api/token", description = "用户权限部分")
@RequestMapping("api/token")
public class TokenController {

    @Operation(summary = "获取当前用户信息", security = @SecurityRequirement(name = "auth"))
    @GetMapping("/current")
    public Result<String> getCurrentAdminUser() {
        return ResUtil.ok("hello world");
    }
}
