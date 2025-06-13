package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.project.dashboard.*;
import com.zkyzn.project_manager.stories.ProjectDashboardStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * @author: Mr-ti
 * Date: 2025/6/13 18:02
 */
@RestController
@Tag(name = "api/project/dashboard", description = "项目看板管理")
@RequestMapping("api/project/dashboard")
public class ProjectDashboardController {

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private ProjectDashboardStory projectDashboardStory;

    @Operation(summary = "获取项目进度概览")
    @GetMapping("/{projectNumber}/progress-overview")
    public Result<ProgressOverview> getProgressOverview(
            @PathVariable("projectNumber") String projectNumber) {

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo == null) {
            return ResUtil.fail("项目不存在");
        }

        return ResUtil.ok(projectDashboardStory.getProgressOverview(projectInfo.getProjectId()));
    }

    @Operation(summary = "获取科室月进度")
    @GetMapping("/{projectNumber}/department-progress")
    public Result<List<DepartmentProgress>> getDepartmentProgress(
            @PathVariable("projectNumber") String projectNumber) {

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);;
        if (projectInfo == null) {
            return ResUtil.fail("项目不存在");
        }

        return ResUtil.ok(projectDashboardStory.getDepartmentProgress(projectInfo.getProjectId()));
    }

    @Operation(summary = "获取主要风险项")
    @GetMapping("/{projectNumber}/risk-items")
    public Result<List<RiskItem>> getRiskItems(
            @PathVariable("projectNumber") String projectNumber) {

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo == null) {
            return ResUtil.fail("项目不存在");
        }

        return ResUtil.ok(projectDashboardStory.getRiskItems(projectInfo.getProjectId()));
    }

    @Operation(summary = "获取回款计划")
    @GetMapping("/{projectNumber}/payment-plans")
    public Result<List<PaymentPlan>> getPaymentPlans(
            @PathVariable("projectNumber") String projectNumber) {

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo == null) {
            return ResUtil.fail("项目不存在");
        }

        return ResUtil.ok(projectDashboardStory.getPaymentPlans(projectInfo.getProjectId()));
    }

    @Operation(summary = "获取待办事项")
    @GetMapping("/{projectNumber}/upcoming-tasks")
    public Result<List<UpcomingTask>> getUpcomingTasks(
            @PathVariable("projectNumber") String projectNumber) {

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo == null) {
            return ResUtil.fail("项目不存在");
        }

        return ResUtil.ok(projectDashboardStory.getUpcomingTasks(projectInfo.getProjectId()));
    }

    @Operation(summary = "获取计划变更清单")
    @GetMapping("/{projectNumber}/change-records")
    public Result<List<ChangeRecord>> getChangeRecords(
            @PathVariable("projectNumber") String projectNumber) {

        ProjectInfo projectInfo = projectInfoService.getByProjectNumber(projectNumber);
        if (projectInfo == null) {
            return ResUtil.fail("项目不存在");
        }

        return ResUtil.ok(projectDashboardStory.getChangeRecords(projectInfo.getProjectId()));
    }
}
