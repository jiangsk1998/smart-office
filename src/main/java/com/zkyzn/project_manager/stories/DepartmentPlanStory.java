package com.zkyzn.project_manager.stories;

import com.zkyzn.project_manager.models.Department;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.services.DepartmentService;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.so.department.DepartmentProjectProgressResp;
import com.zkyzn.project_manager.so.department.DepartmentTaskStatsResp;
import com.zkyzn.project_manager.so.department.DepartmentWeeklyProgressResp;
import com.zkyzn.project_manager.so.department.DepartmentWeeklyTaskStatsResp;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * 封装与科室计划相关的业务场景
 */
@Service
public class DepartmentPlanStory {

    @Resource
    private ProjectPlanService projectPlanService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ProjectInfoService projectInfoService;

    public DepartmentTaskStatsResp getDepartmentTaskStats(String departmentName) {
        DepartmentTaskStatsResp response = new DepartmentTaskStatsResp();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 1. 今日到期数
        long todayDueCount = projectPlanService.countTasksDueOnDate(departmentName, today);
        response.setTodayDueCount(todayDueCount);

        // 2. 今日到期与昨日到期变化百分比
        long yesterdayDueCount = projectPlanService.countTasksDueOnDate(departmentName, yesterday);
        if (yesterdayDueCount > 0) {
            BigDecimal change = BigDecimal.valueOf(todayDueCount - yesterdayDueCount)
                    .divide(BigDecimal.valueOf(yesterdayDueCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setChangePercentage(String.format("%+.2f%%", change));
        } else if (todayDueCount > 0) {
            response.setChangePercentage("+100.00%");
        } else {
            response.setChangePercentage("0.00%");
        }

        // 3. 近10日的每日到期数
        List<DepartmentTaskStatsResp.DailyCount> dailyCounts = new ArrayList<>();
        for (int i = 9; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = projectPlanService.countTasksDueOnDate(departmentName, date);
            dailyCounts.add(new DepartmentTaskStatsResp.DailyCount(date, count));
        }
        response.setLast10DaysDueCounts(dailyCounts);

        return response;
    }

    public DepartmentWeeklyTaskStatsResp getDepartmentWeeklyTaskStats(String departmentName) {
        DepartmentWeeklyTaskStatsResp response = new DepartmentWeeklyTaskStatsResp();
        LocalDate today = LocalDate.now();

        // 定义周的开始和结束（周一到周日）
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 1. 本周到期数
        long thisWeekDueCount = projectPlanService.countTasksDueBetweenDates(departmentName, startOfWeek, endOfWeek);
        response.setThisWeekDueCount(thisWeekDueCount);

        // 2. 与上周到期数比较
        LocalDate startOfLastWeek = startOfWeek.minusWeeks(1);
        LocalDate endOfLastWeek = endOfWeek.minusWeeks(1);
        long lastWeekDueCount = projectPlanService.countTasksDueBetweenDates(departmentName, startOfLastWeek, endOfLastWeek);

        if (lastWeekDueCount > 0) {
            BigDecimal change = BigDecimal.valueOf(thisWeekDueCount - lastWeekDueCount)
                    .divide(BigDecimal.valueOf(lastWeekDueCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setChangePercentage(String.format("%+.2f%%", change));
        } else if (thisWeekDueCount > 0) {
            response.setChangePercentage("+100.00%");
        } else {
            response.setChangePercentage("0.00%");
        }

        // 3. 近10周的每日到期数
        List<DepartmentWeeklyTaskStatsResp.WeeklyCount> weeklyCounts = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (int i = 9; i >= 0; i--) {
            LocalDate weekStartDate = startOfWeek.minusWeeks(i);
            LocalDate weekEndDate = endOfWeek.minusWeeks(i);
            long count = projectPlanService.countTasksDueBetweenDates(departmentName, weekStartDate, weekEndDate);

            // 格式化周的标签，例如 "2025-W23"
            String weekLabel = weekStartDate.getYear() + "-W" + weekStartDate.get(weekFields.weekOfWeekBasedYear());
            weeklyCounts.add(new DepartmentWeeklyTaskStatsResp.WeeklyCount(weekLabel, count));
        }
        response.setLast10WeeksDueCounts(weeklyCounts);

        return response;
    }

    public List<DepartmentProjectProgressResp> getDepartmentProjectMonthlyProgress(Long departmentId) {
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return Collections.emptyList();
        }
        String departmentName = department.getName();

        // 1. 从 plan 表获取本科室参与的所有项目ID
        List<Long> projectIds = projectPlanService.getProjectIdsByDepartment(departmentName);
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.emptyList();
        }

        // 2. 获取这些项目的详细信息用于展示
        List<ProjectInfo> projects = projectInfoService.listByIds(projectIds);

        // 3. 计算当前月份
        LocalDate now = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(now);
        LocalDate firstDayOfMonth = thisMonth.atDay(1);
        LocalDate lastDayOfMonth = thisMonth.atEndOfMonth();

        // 4. 为每个项目计算该科室的专属月进度
        return projects.stream().map(project -> {
            // 4.1 查询该科室在该项目中当月的任务总数
            long totalTasks = projectPlanService.countTasksForDepartmentByDateRange(project.getProjectId(), departmentName, firstDayOfMonth, lastDayOfMonth);

            // 4.2 查询该科室在该项目中当月已完成的任务数
            long completedTasks = projectPlanService.countCompletedTasksForDepartmentByDateRange(project.getProjectId(), departmentName, firstDayOfMonth, lastDayOfMonth);

            DepartmentProjectProgressResp resp = new DepartmentProjectProgressResp();
            resp.setProjectName(project.getProjectName());
            resp.setProjectNumber(project.getProjectNumber());
            resp.setResponsibleLeader(project.getResponsibleLeader());

            // 4.3 组装分数和百分比
            resp.setMonthlyProgressFraction(completedTasks + "/" + totalTasks);
            if (totalTasks > 0) {
                BigDecimal rate = BigDecimal.valueOf(completedTasks)
                        .divide(BigDecimal.valueOf(totalTasks), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                resp.setMonthlyProgress(rate);
            } else {
                resp.setMonthlyProgress(BigDecimal.ZERO);
            }
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 计算单个周期的工作完成进度
     * @param departmentName 科室名称
     * @param weekStartDate 周开始日期
     * @param weekEndDate 周结束日期
     * @return BigDecimal格式的进度百分比
     */
    private BigDecimal calculateWeeklyProgress(String departmentName, LocalDate weekStartDate, LocalDate weekEndDate) {
        // 分母：此时间段内应完成的任务总数
        long shouldCompleteCount = projectPlanService.countTasksDueBetweenDates(departmentName, weekStartDate, weekEndDate);

        if (shouldCompleteCount == 0) {
            return BigDecimal.ZERO;
        }

        // 分子：此时间段内计划完成且实际完成的任务数
        long actualCompletedCount = projectPlanService.countCompletedTasksByEndDateRange(departmentName, weekStartDate, weekEndDate);

        return BigDecimal.valueOf(actualCompletedCount)
                .divide(BigDecimal.valueOf(shouldCompleteCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 获取科室周工作完成进度统计
     * @param departmentName 科室名称
     * @return 进度统计响应体
     */
    public DepartmentWeeklyProgressResp getDepartmentWeeklyProgress(String departmentName) {
        DepartmentWeeklyProgressResp response = new DepartmentWeeklyProgressResp();
        LocalDate today = LocalDate.now();

        // 1. 计算上周的进度
        LocalDate lastWeekStart = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastWeekEnd = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        BigDecimal lastWeekProgress = calculateWeeklyProgress(departmentName, lastWeekStart, lastWeekEnd);
        response.setLastWeekProgress(lastWeekProgress);

        // 2. 计算上上周的进度并得出变化率
        LocalDate twoWeeksAgoStart = today.minusWeeks(2).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate twoWeeksAgoEnd = today.minusWeeks(2).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        BigDecimal twoWeeksAgoProgress = calculateWeeklyProgress(departmentName, twoWeeksAgoStart, twoWeeksAgoEnd);

        if (twoWeeksAgoProgress.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal change = lastWeekProgress.subtract(twoWeeksAgoProgress)
                    .divide(twoWeeksAgoProgress, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setChangePercentage(String.format("%+.2f%%", change));
        } else if (lastWeekProgress.compareTo(BigDecimal.ZERO) > 0) {
            response.setChangePercentage("+100.00%");
        } else {
            response.setChangePercentage("0.00%");
        }

        // 3. 计算本周之前的十周工作进度
        List<DepartmentWeeklyProgressResp.WeeklyProgress> progressList = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (int i = 10; i >= 1; i--) {
            LocalDate weekStartDate = today.minusWeeks(i).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate weekEndDate = today.minusWeeks(i).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            BigDecimal progress = calculateWeeklyProgress(departmentName, weekStartDate, weekEndDate);

            String weekLabel = weekStartDate.getYear() + "-W" + weekStartDate.get(weekFields.weekOfWeekBasedYear());
            progressList.add(new DepartmentWeeklyProgressResp.WeeklyProgress(weekLabel, progress));
        }
        response.setLast10WeeksProgress(progressList);

        return response;
    }

}