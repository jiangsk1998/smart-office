package com.zkyzn.project_manager.controllers;

import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.personnel.*;
import com.zkyzn.project_manager.stories.PersonnelPlanStory;
import com.zkyzn.project_manager.utils.ResUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "api/personnel/plan", description = "个人计划")
@RequestMapping("api/personnel/plan")
public class PersonnelPlanController {

    @Resource
    private PersonnelPlanStory personnelPlanStory;

    @Operation(summary = "今日到期事项")
    @GetMapping(value = "/{personName}/daily/stats")
    public Result<PersonnelDailyTaskStatsResp> getPersonnelDailyTaskStats(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelDailyTaskStats(personName));
    }

    @Operation(summary = "本周到期事项")
    @GetMapping(value = "/{personName}/weekly/stats")
    public Result<PersonnelWeeklyTaskStatsResp> getPersonnelWeeklyTaskStats(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelWeeklyTaskStats(personName));
    }

    @Operation(summary = "本周工作完成进度")
    @GetMapping(value = "/{personName}/weekly/progress")
    public Result<PersonnelWeeklyProgressResp> getPersonnelWeeklyProgress(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelWeeklyProgress(personName));
    }

    @Operation(summary = "月工作完成进度")
    @GetMapping(value = "/{personName}/monthly/progress")
    public Result<PersonnelMonthlyProgressResp> getPersonnelMonthlyProgress(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelMonthlyProgressStats(personName));
    }

    @Operation(summary = "上周未完成事项")
    @GetMapping(value = "/{personName}/lastWeekUncompleted")
    public Result<PersonnelWeeklyUncompletedStatsResp> getPersonnelWeeklyUncompletedStats(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelWeeklyUncompletedStats(personName));
    }

    @Operation(summary = "个人待办事项")
    @GetMapping(value = "/{personName}/todo/tasks")
    public ResultList<PersonnelTodoTaskResp> getPersonnelTodoTasks(
            @PathVariable("personName") String personName,
            @RequestParam(value = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(
                    name = "start_date",
                    description = "筛选开始日期，格式yyyy-MM-dd",
                    example = "2023-01-01",
                    required = false
            )

            LocalDate startDate,
            @RequestParam(value = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(
                    name = "end_date",
                    description = "筛选结束日期，格式yyyy-MM-dd",
                    example = "2023-01-01",
                    required = false
            )
            LocalDate endDate
    ) {
        List<PersonnelTodoTaskResp> result = personnelPlanStory.getPersonnelTodoTasks(personName, startDate, endDate);
        return ResUtil.list(result);
    }

}