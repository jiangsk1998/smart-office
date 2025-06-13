package com.zkyzn.project_manager.services;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.mappers.ProjectPlanDao;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.department.DepartmentWeeklyProgressResp;
import com.zkyzn.project_manager.so.project_info.ProjectDetailResp.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Mr-ti
 * Date: 2025/6/8 23:02
 */
@Service
public class ProjectPlanService extends MPJBaseServiceImpl<ProjectPlanDao, ProjectPlan> {
    /**
     * 根据项目id删除项目计划
     */
    public void removeByProjectId(Long projectId) {
        this.lambdaUpdate().eq(ProjectPlan::getProjectId, projectId).remove();
    }

    public List<ProjectPlan> getPlansByProjectId(Long projectId) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .orderByAsc("task_order");
        return baseMapper.selectList(wrapper);
    }

    public long countByProjectIdAndStatus(Long projectId, String status) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("task_status", status);
        return baseMapper.selectCount(wrapper);
    }

    public long countByProjectId(Long projectId) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId);
        return baseMapper.selectCount(wrapper);
    }

    public long countByDateRange(Long projectId, LocalDate start, LocalDate end) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .ge("end_date", start)
                .le("end_date", end);
        return baseMapper.selectCount(wrapper);
    }

    public long countCompletedByDateRange(Long projectId, LocalDate start, LocalDate end) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("task_status", "已完成")
                .ge("end_date", start)
                .le("end_date", end);
        return baseMapper.selectCount(wrapper);
    }

    public long countLastWeekCompleted(Long projectId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfLastWeek = now.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endOfLastWeek = startOfLastWeek.plusDays(6);

        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("task_status", "已完成")
                .ge("real_end_date", startOfLastWeek)
                .le("real_end_date", endOfLastWeek);
        return baseMapper.selectCount(wrapper);
    }

    public long countDelayedTasks(Long projectId) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .ne("task_status", "已完成")
                .lt("end_date", LocalDate.now());
        return baseMapper.selectCount(wrapper);
    }

    public Map<String, BigDecimal> getDepartmentProgress(Long projectId) {

        Map<String, BigDecimal> progressMap = new HashMap<>();

        // todo: 获取科室进度

        return progressMap;
    }

    public List<ProjectPlan> getUpcomingTasks(Long projectId, int days) {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(days);

        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .ne("task_status", "已完成")
                .ge("end_date", now)
                .le("end_date", endDate)
                .orderByAsc("end_date");
        return baseMapper.selectList(wrapper);
    }

    public List<ChangeRecord> getChangeRecords(Long projectId) {
        // todo: 获取变更记录

        return null;
    }

    public List<ProjectPlan> getPlansByPhase(Long projectId, String phaseName) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("task_package", phaseName)
                .orderByAsc("task_order");
        return baseMapper.selectList(wrapper);
    }

    public long countTasksDueOnDate(String departmentName, LocalDate date) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("end_date", date);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定科室在日期范围内的到期任务总数
     */
    public long countTasksDueBetweenDates(String departmentName, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .ge("end_date", startDate)
                .le("end_date", endDate);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 根据科室名称获取其参与的所有不重复的项目ID列表
     * @param departmentName 科室名称
     * @return 项目ID列表
     */
    public List<Long> getProjectIdsByDepartment(String departmentName) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT project_id").eq("department", departmentName);
        List<Object> projectIdsAsObjects = baseMapper.selectObjs(wrapper);
        return projectIdsAsObjects.stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .collect(Collectors.toList());
    }

    /**
     * 统计指定项目中，特定科室在日期范围内的任务总数
     */
    public long countTasksForDepartmentByDateRange(Long projectId, String departmentName, LocalDate start, LocalDate end) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("department", departmentName)
                .ge("end_date", start)
                .le("end_date", end);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定项目中，特定科室在日期范围内已完成的任务数
     */
    public long countCompletedTasksForDepartmentByDateRange(Long projectId, String departmentName, LocalDate start, LocalDate end) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("department", departmentName)
                .eq("task_status", "已完成")
                .ge("end_date", start)
                .le("end_date", end);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定科室在日期范围内计划完成且状态为“已完成”的任务数
     */
    public long countCompletedTasksByEndDateRange(String departmentName, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("task_status", "已完成") // 增加了“已完成”的状态筛选
                .ge("end_date", startDate)
                .le("end_date", endDate);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定科室，在某日期范围（planStartDate-planEndDate）内计划完成，
     * 并在某个截止日期（realEndDateCutoff）前实际完成的任务数
     */
    public long countCompletedTasksByDateRanges(String departmentName, LocalDate planStartDate, LocalDate planEndDate, LocalDate realEndDateCutoff) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("task_status", "已完成")
                .ge("end_date", planStartDate)  // 任务应在本月内完成
                .le("end_date", planEndDate)
                .le("real_end_date", realEndDateCutoff); // 任务在指定日期或之前实际完成
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定科室在某月内计划完成，但当前已拖期的任务数
     * 拖期定义: 已到截止时间，但任务不处于“已完成”或者“中止”状态
     */
    public long countDelayedTasksForMonth(String departmentName, LocalDate monthStartDate, LocalDate monthEndDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .ge("end_date", monthStartDate)
                .le("end_date", monthEndDate)
                .lt("end_date", LocalDate.now()) // 关键：截止日期已过
                .notIn("task_status", Arrays.asList(TaskStatusEnum.COMPLETED.toString(), TaskStatusEnum.STOP.toString())); // 关键：状态不是“已完成”或“中止”
        return baseMapper.selectCount(wrapper);
    }


}
