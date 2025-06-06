package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "api/drawing/plan", description = "图纸计划相关")
@RequestMapping("api/drawing/plan")
public class DrawingPlanController {

    @Operation(summary = "生成新的图纸计划")
    @PostMapping(value = "/generate")
    public Result<String> dirPersonFolder(
            @RequestParam(defaultValue = "/") String path
    ) {
        return ResUtil.ok("ok");
    }
}
