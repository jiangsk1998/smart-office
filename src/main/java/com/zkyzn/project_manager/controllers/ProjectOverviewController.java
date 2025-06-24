package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.project.overview.ProjectOverviewResponse;
import com.zkyzn.project_manager.stories.ProjectOverviewStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Mr-ti
 * Date: 2025/6/24 19:35
 */
@RestController
@RequestMapping("/api/project/overview")
@Tag(name = "项目总览", description = "项目总体统计信息接口")
public class ProjectOverviewController {

    @Resource
    private ProjectOverviewStory projectOverviewStory;

    @Operation(summary = "获取项目总览统计信息")
    @GetMapping("/stats")
    public Result<ProjectOverviewResponse> getProjectOverviewStats() {
        ProjectOverviewResponse response = projectOverviewStory.getProjectOverviewStats();
        return ResUtil.ok(response);
    }
}
