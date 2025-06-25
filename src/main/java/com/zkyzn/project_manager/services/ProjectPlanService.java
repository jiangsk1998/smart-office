package com.zkyzn.project_manager.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.mappers.ProjectPlanDao;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.personnel.PersonnelTodoTaskResp;
import com.zkyzn.project_manager.so.project.ai.MonthlyPlansDTO;
import com.zkyzn.project_manager.so.project.dashboard.ChangeRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
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

    public Page<ProjectPlan> listTasksDueOnDate(Page<ProjectPlan> page, String departmentName, LocalDate date, String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("end_date", date);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定科室在日期范围内的到期任务列表
     */
    public Page<ProjectPlan> listTasksDueBetweenDates(Page<ProjectPlan> page, String departmentName, LocalDate startDate, LocalDate endDate, String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .ge("end_date", startDate)
                .le("end_date", endDate);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定科室在日期范围内计划完成且状态为“已完成”的任务列表
     */
    public Page<ProjectPlan> listCompletedTasksByEndDateRange(Page<ProjectPlan> page, String departmentName, LocalDate startDate, LocalDate endDate, String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
                .ge("end_date", startDate)
                .le("end_date", endDate);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定科室，在某日期范围（planStartDate-planEndDate）内计划完成，
     * 并在某个截止日期（realEndDateCutoff）前实际完成的任务列表
     */
    public Page<ProjectPlan> listCompletedTasksByDateRanges(Page<ProjectPlan> page, String departmentName,
                                                            LocalDate planStartDate, LocalDate planEndDate, LocalDate realEndDateCutoff,
                                                            String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
                .ge("end_date", planStartDate)  // 任务应在本月内完成
                .le("end_date", planEndDate)
                .le("real_end_date", realEndDateCutoff); // 任务在指定日期或之前实际完成
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定科室在某月内计划完成，但当前已拖期的任务列表
     * 拖期定义: 已到截止时间，但任务不处于“已完成”或者“中止”状态
     */
    public Page<ProjectPlan> listDelayedTasksForMonth(Page<ProjectPlan> page, String departmentName, LocalDate monthStartDate, LocalDate monthEndDate, String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("department", departmentName)
                .ge("end_date", monthStartDate)
                .le("end_date", monthEndDate)
                .lt("end_date", LocalDate.now()) // 关键：截止日期已过
                .notIn("task_status", Arrays.asList(TaskStatusEnum.COMPLETED.name(), TaskStatusEnum.STOP.name())); // 关键：状态不是“已完成”或“中止”
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定责任人在特定日期的到期任务列表
     */
    public Page<ProjectPlan> listTasksDueOnDateForPerson(Page<ProjectPlan> page, String personName, LocalDate date, String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .eq("end_date", date);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定责任人在日期范围内的到期任务列表
     */
    public Page<ProjectPlan> listTasksDueBetweenDatesForPerson(Page<ProjectPlan> page, String personName, LocalDate startDate, LocalDate endDate, String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .ge("end_date", startDate)
                .le("end_date", endDate);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定责任人在日期范围内已完成的任务列表
     */
    public Page<ProjectPlan> listCompletedTasksForPersonByDateRange(Page<ProjectPlan> page, String personName, LocalDate startDate, LocalDate endDate, String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
                .ge("end_date", startDate)
                .le("end_date", endDate);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定责任人，在某日期范围（planStartDate-planEndDate）内计划完成，
     * 并在某个截止日期（realEndDateCutoff）前实际完成的任务列表
     */
    public Page<ProjectPlan> listCompletedTasksForPersonByDateRanges(Page<ProjectPlan> page, String personName,
                                                                     LocalDate planStartDate, LocalDate planEndDate, LocalDate realEndDateCutoff,
                                                                     String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .eq("task_status", TaskStatusEnum.COMPLETED.name())
                .ge("end_date", planStartDate)
                .le("end_date", planEndDate)
                .le("real_end_date", realEndDateCutoff);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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
     * 获取指定责任人在某周内计划完成，但当前已拖期的任务列表
     * 拖期/未完成定义: 已到截止时间，但任务不处于“已完成”或者“中止”状态
     */
    public Page<ProjectPlan> listUncompletedTasksForWeek(Page<ProjectPlan> page, String personName, LocalDate weekStartDate, LocalDate weekEndDate, String keyword) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("responsible_person", personName)
                .ge("end_date", weekStartDate)
                .le("end_date", weekEndDate)
                .lt("end_date", LocalDate.now()) // 截止日期已过
                .notIn("task_status", Arrays.asList(TaskStatusEnum.COMPLETED.toString(), TaskStatusEnum.STOP.toString())); // 状态不是“已完成”或“中止”
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("task_description", keyword);
        }
        return baseMapper.selectPage(page, wrapper);
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

    /**
     * 获取本月和下个月的工作计划
     */
    public MonthlyPlansDTO getMonthlyPlans() {
        LocalDate now = LocalDate.now();

        // 计算本月范围
        YearMonth currentMonth = YearMonth.from(now);
        LocalDate firstDayOfCurrentMonth = currentMonth.atDay(1);
        LocalDate lastDayOfCurrentMonth = currentMonth.atEndOfMonth();

        // 计算下个月范围
        YearMonth nextMonth = currentMonth.plusMonths(1);
        LocalDate firstDayOfNextMonth = nextMonth.atDay(1);
        LocalDate lastDayOfNextMonth = nextMonth.atEndOfMonth();

        // 查询本月计划
        List<ProjectPlan> currentMonthPlans = getPlansByEndDateRange(firstDayOfCurrentMonth, lastDayOfCurrentMonth);

        // 查询下个月计划
        List<ProjectPlan> nextMonthPlans = getPlansByEndDateRange(firstDayOfNextMonth, lastDayOfNextMonth);

        // 创建返回对象
        MonthlyPlansDTO dto = new MonthlyPlansDTO();
        dto.setCurrentMonthPlans(currentMonthPlans);
        dto.setNextMonthPlans(nextMonthPlans);

        return dto;
    }

    /**
     * 根据结束日期范围查询计划
     */
    private List<ProjectPlan> getPlansByEndDateRange(LocalDate start, LocalDate end) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectPlan.class)
                .between(ProjectPlan::getEndDate, start, end)
                .orderByAsc(ProjectPlan::getEndDate);  // 按结束日期升序排序

        return baseMapper.selectList(wrapper);
    }
    /**
     * 获取指定责任人在日期范围内的任务列表。
     * 单表查询。
     * @param personName 责任人姓名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 任务列表
     */
    public List<ProjectPlan> getPlansByDateRangeForPerson(String personName, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ProjectPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPlan::getResponsiblePerson, personName)
                .ge(ProjectPlan::getEndDate, startDate)
                .le(ProjectPlan::getEndDate, endDate)
                .orderByAsc(ProjectPlan::getEndDate);
        return baseMapper.selectList(wrapper); // 使用 selectList 进行单表查询
    }

    /**
     * 获取指定责任人在日期范围内且指定状态的任务列表。
     * 单表查询。
     * @param personName 责任人姓名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 任务状态
     * @return 任务列表
     */
    public List<ProjectPlan> getPlansByDateRangeAndStatusForPerson(String personName, LocalDate startDate, LocalDate endDate, String status) {
        LambdaQueryWrapper<ProjectPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPlan::getResponsiblePerson, personName)
                .eq(ProjectPlan::getTaskStatus, status)
                .ge(ProjectPlan::getRealEndDate, startDate)
                .le(ProjectPlan::getRealEndDate, endDate)
                .orderByAsc(ProjectPlan::getRealEndDate);
        return baseMapper.selectList(wrapper); // 使用 selectList 进行单表查询
    }

    /**
     * 获取指定责任人在日期范围内，且不处于指定“已完成”和“中止”状态的任务列表。
     * 单表查询。
     * @param personName 责任人姓名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param completedStatus 已完成状态
     * @param stopStatus 中止状态
     * @return 任务列表
     */
    public List<ProjectPlan> getPlansByDateRangeAndUncompletedStatusForPerson(String personName, LocalDate startDate, LocalDate endDate, String completedStatus, String stopStatus) {
        LambdaQueryWrapper<ProjectPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPlan::getResponsiblePerson, personName)
                .ge(ProjectPlan::getEndDate, startDate)
                .le(ProjectPlan::getEndDate, endDate)
                .notIn(ProjectPlan::getTaskStatus, Arrays.asList(completedStatus, stopStatus))
                .orderByAsc(ProjectPlan::getEndDate);
        return baseMapper.selectList(wrapper); // 使用 selectList 进行单表查询
    }

    /**
     * 计算指定人员、日期范围和状态的任务数量
     */
    public Integer countPlansByDateRangeAndStatusForPerson(
            String responsiblePerson, LocalDate startDate, LocalDate endDate, String status) { // 修正参数名
        return baseMapper.countPlansByDateRangeAndStatusForPerson(responsiblePerson, startDate, endDate, status);
    }

    /**
     * 计算指定人员、日期范围和非指定状态的任务数量 (用于未完成/中止)
     */
    public Integer countPlansByDateRangeAndUncompletedStatusForPerson(
            String responsiblePerson, LocalDate startDate, LocalDate endDate, String completedStatus, String stopStatus) { // 修正参数名
        return baseMapper.countPlansByDateRangeAndUncompletedStatusForPerson(responsiblePerson, startDate, endDate, completedStatus, stopStatus);
    }

    /**
     * 计算指定人员、日期范围内的所有任务数量
     */
    public Integer countPlansByDateRangeForPerson(String responsiblePerson, LocalDate startDate, LocalDate endDate) { // 修正参数名
        return baseMapper.countPlansByDateRangeForPerson(responsiblePerson, startDate, endDate);
    }


    public Set<String> findActiveResponsiblePersons() {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan::getResponsiblePerson)
                .ne(ProjectPlan::getTaskStatus, TaskStatusEnum.COMPLETED.name());
        return baseMapper.selectList(wrapper).stream()
                .map(ProjectPlan::getResponsiblePerson)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    public int countActiveResponsiblePersonsByDate(LocalDate date) {
        MPJLambdaQueryWrapper<ProjectPlan> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.select(ProjectPlan::getResponsiblePerson)
                .ne(ProjectPlan::getTaskStatus, TaskStatusEnum.COMPLETED.name())
                .le(ProjectPlan::getStartDate, date)
                .ge(ProjectPlan::getEndDate, date);
        return baseMapper.selectList(wrapper).size();
    }
}