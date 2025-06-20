package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.mappers.ProjectPlanDao;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.personnel.PersonnelTodoTaskResp;
import com.zkyzn.project_manager.so.project.dashboard.ChangeRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                .eq(ProjectPlan::getTaskStatus, TaskStatusEnum.COMPLETED.name())
                .ge(ProjectPlan::getRealEndDate, startOfLastWeek)
                .le(ProjectPlan::getRealEndDate, endOfLastWeek);

        return baseMapper.selectCount(wrapper);
    }

    public long countDelayedTasks(Long projectId) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan:: getProjectPlanId)
                .eq(ProjectPlan::getProjectId, projectId)
                .ne(ProjectPlan::getTaskStatus, TaskStatusEnum.COMPLETED.name())
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
                .ne(ProjectPlan::getTaskStatus, TaskStatusEnum.COMPLETED.name())
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
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
                .ge("real_end_date", start)
                .le("real_end_date", end)
                .groupBy("department");

        return baseMapper.selectMaps(wrapper);
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
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
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
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
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
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
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
                .notIn("task_status", Arrays.asList(TaskStatusEnum.COMPLETED.name(), TaskStatusEnum.STOP.name())); // 关键：状态不是“已完成”或“中止”
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 获取指定科室的所有不重复的责任人列表
     * @param departmentName 科室名称
     * @return 责任人姓名列表
     */
    public List<String> getUniqueResponsiblePersonsByDepartment(String departmentName) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT responsible_person").eq("department", departmentName);
        List<Object> responsiblePersonsAsObjects = baseMapper.selectObjs(wrapper);
        return responsiblePersonsAsObjects.stream()
                .map(obj -> obj != null ? obj.toString() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 统计指定科室中特定人员在日期范围内的应完成任务总数
     */
    public long countTasksForPersonByDateRange(String departmentName, String personName, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("responsible_person", personName)
                .ge("end_date", startDate)
                .le("end_date", endDate);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定科室中特定人员在日期范围内已完成的任务数
     */
    public long countCompletedTasksForPersonByDateRange(String departmentName, String personName, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("responsible_person", personName)
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
                .ge("end_date", startDate)
                .le("end_date", endDate);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定责任人在特定日期的到期任务数
     */
    public long countTasksDueOnDateForPerson(String personName, LocalDate date) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .eq("end_date", date);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定责任人在日期范围内的到期任务总数
     */
    public long countTasksDueBetweenDatesForPerson(String personName, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .ge("end_date", startDate)
                .le("end_date", endDate);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定责任人在日期范围内的应完成任务总数
     */
    public long countTasksForPersonByDateRange(String personName, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .ge("end_date", startDate)
                .le("end_date", endDate);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定责任人在日期范围内已完成的任务数
     */
    public long countCompletedTasksForPersonByDateRange(String personName, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
                .ge("end_date", startDate)
                .le("end_date", endDate);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定责任人，在某日期范围（planStartDate-planEndDate）内计划完成，
     * 并在某个截止日期（realEndDateCutoff）前实际完成的任务数
     */
    public long countCompletedTasksForPersonByDateRanges(String personName, LocalDate planStartDate, LocalDate planEndDate, LocalDate realEndDateCutoff) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
                .ge("end_date", planStartDate)
                .le("end_date", planEndDate)
                .le("real_end_date", realEndDateCutoff);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定责任人在某周内计划完成，但当前已拖期的任务数
     * 拖期/未完成定义: 已到截止时间，但任务不处于“已完成”或者“中止”状态
     */
    public long countUncompletedTasksForWeek(String personName, LocalDate weekStartDate, LocalDate weekEndDate) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .ge("end_date", weekStartDate)
                .le("end_date", weekEndDate)
                .lt("end_date", LocalDate.now()) // 截止日期已过
                .notIn("task_status", Arrays.asList(TaskStatusEnum.COMPLETED.toString(), TaskStatusEnum.STOP.toString())); // 状态不是“已完成”或“中止”
        return baseMapper.selectCount(wrapper);
    }


    /**
     * 查询指定责任人在时间范围内的待办事项（未开始或中止）
     * @param personName 责任人姓名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 包含项目信息的待办任务列表
     */
    public List<PersonnelTodoTaskResp> findTodoTasksForPerson(String personName, LocalDate startDate, LocalDate endDate) {
        // 定义待办事项的状态列表
        List<String> statuses = Arrays.asList(TaskStatusEnum.NOT_STARTED.name(), TaskStatusEnum.STOP.name());

        // 直接调用 Mapper 接口中定义的方法
        return baseMapper.findTodoTasksForPersonXML(personName, startDate, endDate, statuses);
    }

    /**
     * 获取指定日期到期但未完成的任务列表。
     *
     * @param dueDate     截止日期
     * @param completedStatus 已完成状态的名称
     * @param stopStatus  中止状态的名称
     * @return 延期任务列表
     */
    public List<ProjectPlan> getDelayedTasks(LocalDate dueDate, String completedStatus, String stopStatus) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectPlan.class) // 选择所有字段
                .eq(ProjectPlan::getEndDate, dueDate) // 结束日期是指定日期
                .notIn(ProjectPlan::getTaskStatus, Arrays.asList(completedStatus, stopStatus)); // 状态不是“已完成”或“中止”
        return baseMapper.selectList(wrapper);
    }
}