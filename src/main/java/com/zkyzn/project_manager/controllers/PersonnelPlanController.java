package com.zkyzn.project_manager.controllers;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;
import com.zkyzn.project_manager.so.personnel.*;
import com.zkyzn.project_manager.stories.PersonnelPlanStory;
import com.zkyzn.project_manager.utils.ResUtil;
import com.zkyzn.project_manager.utils.SecurityUtil;
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
        return ResUtil.ok(personnelPlanStory.getPersonnelDailyTaskStatsByPersonName(personName));
    }

    @Operation(summary = "今日到期事项列表")
    @GetMapping(value = "/{personName}/daily/stats/list")
    public ResultList<ProjectPlan> getPersonnelDailyTaskStatsList(
            @PathVariable("personName") String personName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(personnelPlanStory.getPersonnelDailyTaskStatsListByPersonName(page, personName));
    }

    @Operation(summary = "本周到期事项")
    @GetMapping(value = "/{personName}/weekly/stats")
    public Result<PersonnelWeeklyTaskStatsResp> getPersonnelWeeklyTaskStats(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelWeeklyTaskStatsByPersonName(personName));
    }

    @Operation(summary = "本周到期事项列表")
    @GetMapping(value = "/{personName}/weekly/stats/list")
    public ResultList<ProjectPlan> getPersonnelWeeklyTaskStatsList(
            @PathVariable("personName") String personName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(personnelPlanStory.getPersonnelWeeklyTaskStatsListByPersonName(page, personName));
    }

    @Operation(summary = "本周工作完成进度")
    @GetMapping(value = "/{personName}/weekly/progress")
    public Result<PersonnelWeeklyProgressResp> getPersonnelWeeklyProgress(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelWeeklyProgressByPersonName(personName));
    }

    @Operation(summary = "本周工作完成列表")
    @GetMapping(value = "/{personName}/weekly/progress/list")
    public ResultList<ProjectPlan> getPersonnelWeeklyProgressList(
            @PathVariable("personName") String personName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(personnelPlanStory.getPersonnelWeeklyProgressListByPersonName(page, personName));
    }

    @Operation(summary = "月工作完成进度")
    @GetMapping(value = "/{personName}/monthly/progress")
    public Result<PersonnelMonthlyProgressResp> getPersonnelMonthlyProgress(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelMonthlyProgressStatsByPersonName(personName));
    }

    @Operation(summary = "月工作完成列表")
    @GetMapping(value = "/{personName}/monthly/progress/list")
    public ResultList<ProjectPlan> getPersonnelMonthlyProgressList(
            @PathVariable("personName") String personName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(personnelPlanStory.getPersonnelMonthlyProgressStatsListByPersonName(page, personName));
    }

    @Operation(summary = "上周未完成事项")
    @GetMapping(value = "/{personName}/lastWeekUncompleted")
    public Result<PersonnelWeeklyUncompletedStatsResp> getPersonnelWeeklyUncompletedStats(
            @PathVariable("personName") String personName
    ) {
        return ResUtil.ok(personnelPlanStory.getPersonnelWeeklyUncompletedStatsByPersonName(personName));
    }

    @Operation(summary = "上周未完成事项列表")
    @GetMapping(value = "/{personName}/lastWeekUncompleted/list")
    public ResultList<ProjectPlan> getPersonnelWeeklyUncompletedStatsList(
            @PathVariable("personName") String personName,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        Page<ProjectPlan> page = new Page<>(pageNum, pageSize);
        return ResUtil.list(personnelPlanStory.getPersonnelWeeklyUncompletedStatsListByPersonName(page, personName));
    }

    @Operation(summary = "待办置顶")
    @GetMapping(value = "/todo/task/{taskId}/isTop")
    public Result<Boolean> getPersonnelWeeklyUncompletedStats(
            @PathVariable("taskId") String taskId,@RequestParam Boolean isTop
    ) {
        return ResUtil.ok(personnelPlanStory.todoTaskIsTop(taskId,isTop));
    }


    @Operation(summary = "个人待办事项")
    @GetMapping(value = "/{personName}/todo/tasks")
    public ResultList<PersonnelTodoTaskResp> getPersonnelTodoTasks(
            @PathVariable("personName") String personName,
            @RequestParam(value = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(
                    name = "start_date",
                    description = "筛选开始日期（默认一个月前），格式yyyy-MM-dd",
                    example = "2023-01-01"
            )
            LocalDate startDate,
            @RequestParam(value = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(
                    name = "end_date",
                    description = "筛选结束日期（默认今天），格式yyyy-MM-dd",
                    example = "2023-01-31"
            )
            LocalDate endDate
    ) {
        // 设置默认日期范围：最近一个月
        LocalDate today = LocalDate.now();

        // 处理开始日期默认值
        if (startDate == null) {
            startDate = today.minusMonths(1); // 默认一个月前
        }

        // 处理结束日期默认值
        if (endDate == null) {
            endDate = today; // 默认今天
        }

        // 添加日期范围有效性校验
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        List<PersonnelTodoTaskResp> result = personnelPlanStory.getPersonnelTodoTasks(
                personName, startDate, endDate
        );

        return ResUtil.list(result);
    }

    @GetMapping("/weekly-key-items")
    @Operation(summary = "本周重点事项")
    // 修改返回类型为 List<MessageInfo>
    public Result<List<MessageInfo>> getWeeklyKeyItems() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResUtil.ok(personnelPlanStory.getWeeklyKeyItemsByUserId(currentUserId));
    }

}