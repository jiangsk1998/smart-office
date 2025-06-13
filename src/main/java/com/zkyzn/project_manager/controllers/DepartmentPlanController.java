package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.department.*;
import com.zkyzn.project_manager.stories.DepartmentPlanStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "api/department/plan", description = "科室计划")
@RequestMapping("api/department/plan")
public class DepartmentPlanController {

    @Resource
    private DepartmentPlanStory departmentPlanStory;

    @Operation(summary = "今日到期事项")
    @GetMapping(value = "/{departmentName}/stats")
    public Result<DepartmentTaskStatsResp> getDepartmentTaskStats(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentTaskStats(departmentName));
    }

    @Operation(summary = "项目月进度")
    @GetMapping("/{departmentId}/projects/monthly/progress")
    public ResultList<DepartmentProjectProgressResp> getDepartmentProjectMonthlyProgress(
            @PathVariable Long departmentId
    ) {
        List<DepartmentProjectProgressResp> result = departmentPlanStory.getDepartmentProjectMonthlyProgress(departmentId);
        return ResUtil.list(result);
    }

    @Operation(summary = "本周到期事项")
    @GetMapping(value = "/{departmentName}/weekly/stats")
    public Result<DepartmentWeeklyTaskStatsResp> getDepartmentWeeklyTaskStats(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentWeeklyTaskStats(departmentName));
    }

    @Operation(summary = "周工作完成进度")
    @GetMapping(value = "/{departmentName}/weekly/progress")
    public Result<DepartmentWeeklyProgressResp> getDepartmentWeeklyProgress(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentWeeklyProgress(departmentName));
    }

    @Operation(summary = "月工作完成进度")
    @GetMapping(value = "/{departmentName}/monthly/completion/progress")
    public Result<DepartmentMonthlyProgressResp> getDepartmentMonthlyProgressStats(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentMonthlyProgressStats(departmentName));
    }

    @Operation(summary = "项目拖期项统计")
    @GetMapping(value = "/{departmentName}/monthly/delayed/stats")
    public Result<DepartmentMonthlyDelayedStatsResp> getDepartmentMonthlyDelayedStats(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentMonthlyDelayedStats(departmentName));
    }

    @Operation(summary = "人员月进度")
    @GetMapping("/{departmentName}/personnel-monthly/progress")
    public ResultList<PersonnelMonthlyProgressResp> getPersonnelMonthlyProgress(
            @PathVariable("departmentName") String departmentName
    ) {
        List<PersonnelMonthlyProgressResp> result = departmentPlanStory.getPersonnelMonthlyProgress(departmentName);
        return ResUtil.list(result);
    }

}