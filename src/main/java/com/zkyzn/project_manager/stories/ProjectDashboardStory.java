
package com.zkyzn.project_manager.stories;

import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.models.OperationLog;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.OperationLogService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.so.project.dashboard.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author: Mr-ti
 * Date: 2025/6/13 18:03
 */
@Service
public class ProjectDashboardStory {

    @Resource
    private ProjectPlanService projectPlanService;

    @Resource
    private ProgressHistoryStory progressHistoryService;

    @Resource
    private OperationLogService operationLogService;

    /**
     * 获取项目进度概览
     */
    public ProgressOverview getProgressOverview(Long projectId) {
        ProgressOverview overview = new ProgressOverview();

        // 1. 全周期进度
        overview.setOverallProgress(calculateOverallProgress(projectId));

        // 2. 月进度
        overview.setMonthlyProgress(calculateMonthlyProgress(projectId));

        // 3. 周进度
        overview.setWeeklyProgress(calculateWeeklyProgress(projectId));

        // 4. 上周完成项数量
        LocalDate now = LocalDate.now();
        LocalDate startOfLastWeek = now.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endOfLastWeek = startOfLastWeek.plusDays(6);
        overview.setLastWeekCompletedCount(projectPlanService.countLastWeekCompleted(projectId, startOfLastWeek, endOfLastWeek));

        // 5. 项目拖期项
        overview.setDelayedCompletedCount(projectPlanService.countDelayedTasks(projectId));

        return overview;
    }

    /**
     * 获取科室月进度
     */
    public List<DepartmentProgress> getDepartmentProgress(Long projectId) {
        // 获取当前月份
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();

        // 查询本月所有任务（按科室分组）
        List<Map<String, Object>> results1 = projectPlanService.countTasksByDepartment(projectId, firstDayOfMonth, lastDayOfMonth);
        Map<String, Long> totalTasksByDept = mapResultsToDepartmentCount(results1);


        // 查询本月已完成任务（按科室分组）
        List<Map<String, Object>> results2 = projectPlanService.countCompletedTasksByDepartment(projectId, firstDayOfMonth, lastDayOfMonth);
        Map<String, Long> completedTasksByDept = mapResultsToDepartmentCount(results2);

        // 创建科室进度列表
        List<DepartmentProgress> progressList = new ArrayList<>();

        // 计算各科室进度
        for (String department : totalTasksByDept.keySet()) {
            DepartmentProgress dp = new DepartmentProgress();
            dp.setDepartment(department);

            Long total = totalTasksByDept.get(department);
            Long completed = completedTasksByDept.getOrDefault(department, 0L);

            if (total == 0) {
                dp.setProgressRate(BigDecimal.ZERO);
            } else {
                BigDecimal progress = BigDecimal.valueOf(completed)
                        .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                dp.setProgressRate(progress);
            }

            progressList.add(dp);
        }

        return progressList;
    }



    private Map<String, Long> mapResultsToDepartmentCount(List<Map<String, Object>> results) {
        Map<String, Long> departmentCount = new HashMap<>();

        for (Map<String, Object> result : results) {
            String department = (String) result.get("department");
            Long count = (Long) result.get("task_count");
            if (department != null && count != null) {
                departmentCount.put(department, count);
            }
        }

        return departmentCount;
    }

    /**
     * 获取主要风险项
     */
    public List<RiskItem> getRiskItems(Long projectId) {
        // todo: 实际实现需要从数据库获取
        List<RiskItem> risks = new ArrayList<>();

        RiskItem risk1 = new RiskItem();
        risk1.setRiskName("技术风险");
        risk1.setRiskDetail("关键技术难点尚未突破");
        risks.add(risk1);

        RiskItem risk2 = new RiskItem();
        risk2.setRiskName("资源风险");
        risk2.setRiskDetail("关键技术人员短缺");
        risks.add(risk2);

        return risks;
    }

    /**
     * 获取回款计划
     */
    public List<PaymentPlan> getPaymentPlans(Long projectId) {
        // todo: 实际实现需要从数据库获取
        List<PaymentPlan> payments = new ArrayList<>();

        PaymentPlan plan1 = new PaymentPlan();
        plan1.setItemName("首付款");
        plan1.setPlannedAmount(new BigDecimal("100000.00"));
        plan1.setActualAmount(new BigDecimal("100000.00"));
        plan1.setPlannedDate(LocalDate.now().minusMonths(1));
        payments.add(plan1);

        PaymentPlan plan2 = new PaymentPlan();
        plan2.setItemName("中期款");
        plan2.setPlannedAmount(new BigDecimal("150000.00"));
        plan2.setActualAmount(null);
        plan2.setPlannedDate(LocalDate.now().plusMonths(1));
        payments.add(plan2);

        return payments;
    }

    /**
     * 获取待办事项
     */
    public List<UpcomingTask> getUpcomingTasks(Long projectId) {
        List<ProjectPlan> plans = projectPlanService.getUpcomingTasks(projectId, 7);

        return plans.stream()
                .map(plan -> {
                    UpcomingTask task = new UpcomingTask();
                    task.setTaskContent(plan.getTaskDescription());
                    task.setResponsiblePerson(plan.getResponsiblePerson());
                    task.setEndDate(plan.getEndDate());

                    // 根据结束时间计算优先级
                    long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), plan.getEndDate());
                    if (daysUntilDue <= 1) {
                        task.setPriority("高");
                    } else if (daysUntilDue <= 3) {
                        task.setPriority("中");
                    } else {
                        task.setPriority("低");
                    }

                    return task;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取计划变更清单
     */
    public List<ChangeRecord> getChangeRecords(Long projectId) {

        List<OperationLog> logs = operationLogService.getPlansByPhase(projectId);
        // 将操作日志转变为变更日志
        List<ChangeRecord> records = new ArrayList<>();
        for (OperationLog log : logs) {
            ChangeRecord record = new ChangeRecord();
            record.setItemName(log.getOperateDetail());
            record.setBeforeChange(log.getOriginalData());
            record.setAfterChange(log.getNewData());
            record.setChangeDate(log.getOperateTime());
            record.setChangedBy(log.getOperatorName());
            records.add(record);
        }
        return records;
    }

    /**
     * 计算项目全周期进度
     * @param projectId
     * @return
     */
    private Progress calculateOverallProgress(Long projectId) {
        Long totalTasks = projectPlanService.countByProjectId(projectId);
        Long completedTasks = projectPlanService.countByProjectIdAndStatus(projectId, TaskStatusEnum.COMPLETED.getDisplayName());

        Progress progress = new Progress();
        progress.setCurrentRate(calculateRate(completedTasks, totalTasks));
        progress.setDailyChangeRate(calculateDailyChange(projectId, "overall"));
        return progress;
    }

    /**
     * 计算月度进度
     * @param projectId
     * @return
     */
    private Progress calculateMonthlyProgress(Long projectId) {
        LocalDate now = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(now);
        LocalDate firstDayOfMonth = thisMonth.atDay(1);
        LocalDate lastDayOfMonth = thisMonth.atEndOfMonth();

        Long monthlyTasks = projectPlanService.countByDateRange(projectId, firstDayOfMonth, lastDayOfMonth);
        Long monthlyCompleted = projectPlanService.countCompletedByDateRange(projectId, firstDayOfMonth, lastDayOfMonth);

        Progress progress = new Progress();
        progress.setCurrentRate(calculateRate(monthlyCompleted, monthlyTasks));
        progress.setDailyChangeRate(calculateDailyChange(projectId, "monthly"));
        return progress;
    }

    /**
     * 计算周进度
     * @param projectId
     * @return
     */
    private Progress calculateWeeklyProgress(Long projectId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Long weeklyTasks = projectPlanService.countByDateRange(projectId, startOfWeek, endOfWeek);
        Long weeklyCompleted = projectPlanService.countCompletedByDateRange(projectId, startOfWeek, endOfWeek);

        Progress progress = new Progress();
        progress.setCurrentRate(calculateRate(weeklyCompleted, weeklyTasks));
        progress.setDailyChangeRate(calculateDailyChange(projectId, "weekly"));
        return progress;
    }

    /**
     * 计算进度
     * @param numerator
     * @param denominator
     * @return
     */
    private BigDecimal calculateRate(Long numerator, Long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算每日变化率
     * @param projectId
     * @param type
     * @return
     */
    private BigDecimal calculateDailyChange(Long projectId, String type) {
        LocalDate today = LocalDate.now();

        // 特殊处理：周进度在周一返回0
        if ("weekly".equals(type) && today.getDayOfWeek() == DayOfWeek.MONDAY) {
            return BigDecimal.ZERO;
        }

        // 特殊处理：月进度在1号返回0
        if ("monthly".equals(type) && today.getDayOfMonth() == 1) {
            return BigDecimal.ZERO;
        }

        LocalDate yesterday = today.minusDays(1);

        // 获取昨日进度值（确保只统计到昨天的数据）
        BigDecimal yesterdayRate = progressHistoryService.getProgressRate(projectId, type, yesterday);

        // 获取今日进度值（截止到当前日期）
        BigDecimal todayRate = progressHistoryService.getProgressRate(projectId, type, today);

        // 处理空值情况
        if (yesterdayRate == null) {
            yesterdayRate = BigDecimal.ZERO;
        }
        if (todayRate == null) {
            todayRate = BigDecimal.ZERO;
        }

        // 变化率 = 今日进度 - 昨日进度
        return todayRate.subtract(yesterdayRate);
    }
}