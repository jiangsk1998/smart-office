package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.DrawingPlan;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.stories.DrawingPlanStory;
import com.zkyzn.project_manager.utils.ResUtil;
import com.zkyzn.project_manager.utils.UrlUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@Tag(name = "api/drawing/plan", description = "图纸计划相关")
@RequestMapping("api/drawing/plan")
public class DrawingPlanController {

    @Value("${file.base.url}")
    private String baseUrl;

    @Resource
    private DrawingPlanStory drawingPlanStory;

    @Operation(summary = "生成新的图纸计划")
    @PostMapping(value = "/generate")
    public Result<String> generateDrawingPlan(
            @RequestParam(defaultValue = "/") String key,
            @RequestParam() LocalDate start,
            @RequestParam() Long days
    ) throws Exception {
        var relativePathStr = drawingPlanStory.generateDrawingPlan(key, start, days);
        URI uri = UrlUtil.getUrlByRelativePath(baseUrl, relativePathStr);
        return ResUtil.ok(uri.toString());
    }
}
