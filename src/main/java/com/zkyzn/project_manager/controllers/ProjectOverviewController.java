package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.department.plan.DepartmentProjectProgressResp;
import com.zkyzn.project_manager.so.project.overview.DepartmentMonthlyProgress;
import com.zkyzn.project_manager.so.project.overview.ProjectOverviewResponse;
import com.zkyzn.project_manager.stories.DepartmentPlanStory;
import com.zkyzn.project_manager.stories.ProjectOverviewStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Resource
    private DepartmentPlanStory departmentPlanStory;

    @Operation(summary = "获取项目总览统计信息")
    @GetMapping("/stats")
    public Result<ProjectOverviewResponse> getProjectOverviewStats() {
        ProjectOverviewResponse response = projectOverviewStory.getProjectOverviewStats();
        return ResUtil.ok(response);
    }

    @Operation(summary = "获取所有科室当月项目进度")
    @GetMapping("/department-progress")
    public Result<List<DepartmentMonthlyProgress>> getAllDepartmentMonthlyProgress() {
        List<DepartmentMonthlyProgress> progressList =
                projectOverviewStory.getMonthlyDepartmentProgress();
        return ResUtil.ok(progressList);
    }

    @Operation(summary = "项目月进度")
    @GetMapping("/monthly/progress")
    public ResultList<DepartmentProjectProgressResp> getDepartmentProjectMonthlyProgress(
            @RequestParam(value = "department_name", required = false) String departmentName,
            @RequestParam(value = "status", required = false) String status
    ) {
        List<DepartmentProjectProgressResp> result = departmentPlanStory.getDepartmentProjectMonthlyProgressByDepartmentName(departmentName);
        return ResUtil.list(result);
    }
}
