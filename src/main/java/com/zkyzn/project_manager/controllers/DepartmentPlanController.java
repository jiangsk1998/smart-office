package com.zkyzn.project_manager.controllers;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.department.plan.*;
import com.zkyzn.project_manager.so.task.ProjectTaskReq;
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
        return ResUtil.ok(departmentPlanStory.getDepartmentTaskStatsByDepartmentName(departmentName));
    }

    @Operation(summary = "今日到期事项列表")
    @GetMapping(value = "/{departmentName}/stats/list")
    public ResultList<ProjectPlan> getDepartmentTaskStatsList(
            @PathVariable("departmentName") String departmentName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(departmentPlanStory.getDepartmentTaskStatsListByDepartmentName(page, departmentName));
    }

    @Operation(summary = "项目月进度")
    @GetMapping("/{departmentName}/projects/monthly/progress")
    public ResultList<DepartmentProjectProgressResp> getDepartmentProjectMonthlyProgress(
            @PathVariable String departmentName
    ) {
        List<DepartmentProjectProgressResp> result = departmentPlanStory.getDepartmentProjectMonthlyProgressByDepartmentName(departmentName);
        return ResUtil.list(result);
    }

    @Operation(summary = "本周到期事项")
    @GetMapping(value = "/{departmentName}/weekly/stats")
    public Result<DepartmentWeeklyTaskStatsResp> getDepartmentWeeklyTaskStats(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentWeeklyTaskStatsByDepartmentName(departmentName));
    }

    @Operation(summary = "本周到期事项列表")
    @GetMapping(value = "/{departmentName}/weekly/stats/list")
    public ResultList<ProjectPlan> getDepartmentWeeklyTaskStatsList(
            @PathVariable("departmentName") String departmentName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(departmentPlanStory.getDepartmentWeeklyTaskStatsListByDepartmentName(page, departmentName));
    }

    @Operation(summary = "周工作完成进度")
    @GetMapping(value = "/{departmentName}/weekly/progress")
    public Result<DepartmentWeeklyProgressResp> getDepartmentWeeklyProgress(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentWeeklyProgressByDepartmentName(departmentName));
    }

    @Operation(summary = "周工作完成列表")
    @GetMapping(value = "/{departmentName}/weekly/progress/list")
    public ResultList<ProjectPlan> getDepartmentWeeklyProgressList(
            @PathVariable("departmentName") String departmentName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(departmentPlanStory.getDepartmentWeeklyProgressListByDepartmentName(page, departmentName));
    }

    @Operation(summary = "月工作完成进度")
    @GetMapping(value = "/{departmentName}/monthly/completion/progress")
    public Result<DepartmentMonthlyProgressResp> getDepartmentMonthlyProgressStats(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentMonthlyProgressStatsByDepartmentName(departmentName));
    }

    @Operation(summary = "月工作完成列表")
    @GetMapping(value = "/{departmentName}/monthly/completion/progress/list")
    public ResultList<ProjectPlan> getDepartmentMonthlyProgressStatsList(
            @PathVariable("departmentName") String departmentName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(departmentPlanStory.getDepartmentMonthlyProgressStatsListByDepartmentName(page, departmentName));
    }

    @Operation(summary = "项目拖期项统计")
    @GetMapping(value = "/{departmentName}/monthly/delayed/stats")
    public Result<DepartmentMonthlyDelayedStatsResp> getDepartmentMonthlyDelayedStats(
            @PathVariable("departmentName") String departmentName
    ) {
        return ResUtil.ok(departmentPlanStory.getDepartmentMonthlyDelayedStatsByDepartmentName(departmentName));
    }

    @Operation(summary = "项目拖期项列表")
    @GetMapping(value = "/{departmentName}/monthly/delayed/stats/list")
    public ResultList<ProjectPlan> getDepartmentMonthlyDelayedStatsList(
            @PathVariable("departmentName") String departmentName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(departmentPlanStory.getDepartmentMonthlyDelayedStatsListByDepartmentName(page, departmentName));
    }

    @Operation(summary = "人员月进度")
    @GetMapping("/{departmentName}/personnel-monthly/progress")
    public ResultList<PersonnelMonthlyProgressResp> getPersonnelMonthlyProgress(
            @PathVariable("departmentName") String departmentName
    ) {
        List<PersonnelMonthlyProgressResp> result = departmentPlanStory.getPersonnelMonthlyProgressByDepartmentName(departmentName);
        return ResUtil.list(result);
    }

}