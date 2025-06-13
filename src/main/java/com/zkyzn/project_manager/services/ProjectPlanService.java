package com.zkyzn.project_manager.services;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.mappers.ProjectPlanDao;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.project.dashboard.ChangeRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据项目id和状态获取项目计划数量
     * @param projectId
     * @param status
     * @return
     */
    public long countByProjectIdAndStatus(Long projectId, String status) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan:: getProjectPlanId)
                .eq(ProjectPlan::getProjectId, projectId)
                .eq(ProjectPlan::getTaskStatus, status);

        return baseMapper.selectCount(wrapper);
    }

    /**
     * 根据项目id获取项目计划数量
     * @param projectId
     * @return
     */
    public long countByProjectId(Long projectId) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan:: getProjectPlanId)
                .eq(ProjectPlan::getProjectId, projectId);

        return baseMapper.selectCount(wrapper);
    }

    /**
     * 根据项目id、状态和实际完成时间获取项目计划数量
     * @param projectId
     * @return
     */
    public long countByProjectIdTaskStatusAndRealEndDate(Long projectId, String status, LocalDate date) {
        // 查询截止到该日期的完成数
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan:: getProjectPlanId)
                .eq(ProjectPlan::getProjectId, projectId)
                .eq(ProjectPlan::getTaskStatus, status)
                .eq(ProjectPlan::getRealEndDate, date);

        return baseMapper.selectCount(wrapper);
    }

    /**
     * 根据项目id和日期范围获取项目计划完成计划数量
     * @param projectId
     * @param start
     * @param end
     * @return
     */
    public long countByDateRange(Long projectId, LocalDate start, LocalDate end) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan:: getProjectPlanId)
                .eq(ProjectPlan::getProjectId, projectId)
                .ge(ProjectPlan::getEndDate, start)
                .le(ProjectPlan::getEndDate, end);

        return baseMapper.selectCount(wrapper);
    }

    /**
     * 根据项目id和日期范围获取项目已完成计划数量
     * @param projectId
     * @param start
     * @param end
     * @return
     */
    public long countCompletedByDateRange(Long projectId, LocalDate start, LocalDate end) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan:: getProjectPlanId)
                .eq(ProjectPlan::getProjectId, projectId)
                .ge(ProjectPlan::getRealEndDate, start)
                .le(ProjectPlan::getRealEndDate, end);

        return baseMapper.selectCount(wrapper);
    }

    public long countLastWeekCompleted(Long projectId, LocalDate startOfLastWeek, LocalDate endOfLastWeek) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan:: getProjectPlanId)
                .eq(ProjectPlan::getProjectId, projectId)
                .eq(ProjectPlan::getTaskStatus, TaskStatusEnum.COMPLETED.getDisplayName())
                .ge(ProjectPlan::getRealEndDate, startOfLastWeek)
                .le(ProjectPlan::getRealEndDate, endOfLastWeek);

        return baseMapper.selectCount(wrapper);
    }

    public long countDelayedTasks(Long projectId) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan:: getProjectPlanId)
                .eq(ProjectPlan::getProjectId, projectId)
                .ne(ProjectPlan::getTaskStatus, TaskStatusEnum.COMPLETED.getDisplayName())
                .lt(ProjectPlan::getEndDate, LocalDate.now());

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

        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectPlan.class)
                .eq(ProjectPlan::getProjectId, projectId)
                .ne(ProjectPlan::getTaskStatus, TaskStatusEnum.COMPLETED.getDisplayName())
                .ge(ProjectPlan::getEndDate, now)
                .le(ProjectPlan::getEndDate, endDate)
                .orderByAsc(ProjectPlan::getEndDate);

        return baseMapper.selectList(wrapper);
    }

    public List<ChangeRecord> getChangeRecords(Long projectId) {
        // todo: 获取变更记录

        return null;
    }

    public List<ProjectPlan> getPlansByPhase(Long projectId, String phaseName) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectPlan.class)
                .eq(ProjectPlan::getProjectId, projectId)
                .eq(ProjectPlan::getTaskPackage, phaseName)
                .orderByAsc(ProjectPlan::getStartDate);

        return baseMapper.selectList(wrapper);
    }

    public long countTasksDueOnDate(String departmentName, LocalDate date) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("end_date", date);
        return baseMapper.selectCount(wrapper);
    }

    public List<Map<String, Object>> countTasksByDepartment(Long projectId, LocalDate start, LocalDate end) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.select("department, COUNT(*) as task_count")
                .eq("project_id", projectId)
                .ge("end_date", start)
                .le("end_date", end)
                .groupBy("department");

        return baseMapper.selectMaps(wrapper);
    }

    public List<Map<String, Object>> countCompletedTasksByDepartment(Long projectId, LocalDate start, LocalDate end) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.select("department, COUNT(*) as task_count")
                .eq("project_id", projectId)
                .eq("task_status", "已完成")
                .ge("real_end_date", start)
                .le("real_end_date", end)
                .groupBy("department");

        return baseMapper.selectMaps(wrapper);
    }

}
