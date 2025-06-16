package com.zkyzn.project_manager.stories;

import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.so.personnel.*;
import jakarta.annotation.Resource;
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

/**
 * 封装与个人计划相关的业务场景
 */
@Service
public class PersonnelPlanStory {

    @Resource
    private ProjectPlanService projectPlanService;

    /**
     * 获取个人每日到期任务统计
     * @param personName 责任人姓名
     * @return 每日任务统计
     */
    public PersonnelDailyTaskStatsResp getPersonnelDailyTaskStatsByPersonName(String personName) {
        PersonnelDailyTaskStatsResp response = new PersonnelDailyTaskStatsResp();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 1. 今日到期数
        long todayDueCount = projectPlanService.countTasksDueOnDateForPerson(personName, today);
        response.setTodayDueCount(todayDueCount);

        // 2. 与昨日到期数比较
        long yesterdayDueCount = projectPlanService.countTasksDueOnDateForPerson(personName, yesterday);
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
        List<PersonnelDailyTaskStatsResp.DailyCount> dailyCounts = new ArrayList<>();
        for (int i = 9; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = projectPlanService.countTasksDueOnDateForPerson(personName, date);
            dailyCounts.add(new PersonnelDailyTaskStatsResp.DailyCount(date, count));
        }
        response.setLast10DaysDueCounts(dailyCounts);

        return response;
    }

    /**
     * 获取个人每周到期任务统计
     * @param personName 责任人姓名
     * @return 每周任务统计
     */
    public PersonnelWeeklyTaskStatsResp getPersonnelWeeklyTaskStatsByPersonName(String personName) {
        PersonnelWeeklyTaskStatsResp response = new PersonnelWeeklyTaskStatsResp();
        LocalDate today = LocalDate.now();

        // 定义本周的开始和结束日期（周一到周日）
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 1. 本周到期数
        long thisWeekDueCount = projectPlanService.countTasksDueBetweenDatesForPerson(personName, startOfWeek, endOfWeek);
        response.setThisWeekDueCount(thisWeekDueCount);

        // 2. 与上周到期数比较
        LocalDate startOfLastWeek = startOfWeek.minusWeeks(1);
        LocalDate endOfLastWeek = endOfWeek.minusWeeks(1);
        long lastWeekDueCount = projectPlanService.countTasksDueBetweenDatesForPerson(personName, startOfLastWeek, endOfLastWeek);

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

        // 3. 近10周的每周到期任务数
        List<PersonnelWeeklyTaskStatsResp.WeeklyCount> weeklyCounts = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (int i = 9; i >= 0; i--) {
            LocalDate weekStartDate = startOfWeek.minusWeeks(i);
            LocalDate weekEndDate = endOfWeek.minusWeeks(i);
            long count = projectPlanService.countTasksDueBetweenDatesForPerson(personName, weekStartDate, weekEndDate);

            // 格式化周的标签，例如 "2025-W23"
            String weekLabel = weekStartDate.getYear() + "-W" + weekStartDate.get(weekFields.weekOfWeekBasedYear());
            weeklyCounts.add(new PersonnelWeeklyTaskStatsResp.WeeklyCount(weekLabel, count));
        }
        response.setLast10WeeksDueCounts(weeklyCounts);

        return response;
    }

    /**
     * 计算个人单周的工作完成进度
     * @param personName 责任人姓名
     * @param weekStartDate 周开始日期
     * @param weekEndDate 周结束日期
     * @return BigDecimal格式的进度百分比
     */
    private BigDecimal calculateWeeklyProgressForPerson(String personName, LocalDate weekStartDate, LocalDate weekEndDate) {
        // 分母：此周内应完成的任务总数
        long shouldCompleteCount = projectPlanService.countTasksForPersonByDateRange(personName, weekStartDate, weekEndDate);

        if (shouldCompleteCount == 0) {
            return BigDecimal.ZERO;
        }

        // 分子：此周内计划完成且实际完成的任务数
        long actualCompletedCount = projectPlanService.countCompletedTasksForPersonByDateRange(personName, weekStartDate, weekEndDate);

        return BigDecimal.valueOf(actualCompletedCount)
                .divide(BigDecimal.valueOf(shouldCompleteCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 获取个人周工作完成进度统计
     * @param personName 责任人姓名
     * @return 个人周工作进度
     */
    public PersonnelWeeklyProgressResp getPersonnelWeeklyProgressByPersonName(String personName) {
        PersonnelWeeklyProgressResp response = new PersonnelWeeklyProgressResp();
        LocalDate today = LocalDate.now();

        // 1. 计算上周的进度
        LocalDate lastWeekStart = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastWeekEnd = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        BigDecimal lastWeekProgress = calculateWeeklyProgressForPerson(personName, lastWeekStart, lastWeekEnd);
        response.setLastWeekProgress(lastWeekProgress);

        // 2. 计算上上周的进度并得出变化率
        LocalDate twoWeeksAgoStart = today.minusWeeks(2).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate twoWeeksAgoEnd = today.minusWeeks(2).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        BigDecimal twoWeeksAgoProgress = calculateWeeklyProgressForPerson(personName, twoWeeksAgoStart, twoWeeksAgoEnd);

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
        List<PersonnelWeeklyProgressResp.WeeklyProgress> progressList = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate currentWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int i = 10; i >= 1; i--) {
            LocalDate weekStartDate = currentWeekStart.minusWeeks(i);
            LocalDate weekEndDate = weekStartDate.plusDays(6);
            BigDecimal progress = calculateWeeklyProgressForPerson(personName, weekStartDate, weekEndDate);

            String weekLabel = weekStartDate.getYear() + "-W" + weekStartDate.get(weekFields.weekOfWeekBasedYear());
            progressList.add(new PersonnelWeeklyProgressResp.WeeklyProgress(weekLabel, progress));
        }
        response.setLast10WeeksProgress(progressList);

        return response;
    }

    /**
     * 获取个人月度工作完成进度
     * @param personName 责任人姓名
     * @return 个人月度进度统计
     */
    public PersonnelMonthlyProgressResp getPersonnelMonthlyProgressStatsByPersonName(String personName) {
        PersonnelMonthlyProgressResp response = new PersonnelMonthlyProgressResp();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 定义本月的开始和结束日期
        YearMonth thisMonth = YearMonth.from(today);
        LocalDate firstDayOfMonth = thisMonth.atDay(1);
        LocalDate lastDayOfMonth = thisMonth.atEndOfMonth();

        // 分母：获取该人员本月应完成的总任务数
        long totalTasksInMonth = projectPlanService.countTasksForPersonByDateRange(personName, firstDayOfMonth, lastDayOfMonth);

        if (totalTasksInMonth == 0) {
            response.setThisMonthProgress(BigDecimal.ZERO);
            response.setDailyChangePercentage("0.00%");
            response.setLast10DaysProgress(Collections.emptyList());
            return response;
        }

        // 1. 计算今天的月度进度
        long completedTasksToday = projectPlanService.countCompletedTasksForPersonByDateRanges(personName, firstDayOfMonth, lastDayOfMonth, today);
        BigDecimal todayProgress = BigDecimal.valueOf(completedTasksToday)
                .divide(BigDecimal.valueOf(totalTasksInMonth), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        response.setThisMonthProgress(todayProgress);

        // 2. 计算与昨日的进度变化
        long completedTasksYesterday = projectPlanService.countCompletedTasksForPersonByDateRanges(personName, firstDayOfMonth, lastDayOfMonth, yesterday);
        BigDecimal yesterdayProgress = BigDecimal.valueOf(completedTasksYesterday)
                .divide(BigDecimal.valueOf(totalTasksInMonth), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (yesterdayProgress.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal change = todayProgress.subtract(yesterdayProgress)
                    .divide(yesterdayProgress, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setDailyChangePercentage(String.format("%+.2f%%", change));
        } else if (todayProgress.compareTo(BigDecimal.ZERO) > 0) {
            response.setDailyChangePercentage("+100.00%");
        } else {
            response.setDailyChangePercentage("0.00%");
        }

        // 3. 计算近10日的日进度
        List<PersonnelMonthlyProgressResp.DailyProgress> dailyProgressList = new ArrayList<>();
        for (int i = 9; i >= 0; i--) {
            LocalDate currentDate = today.minusDays(i);
            long completedCount = projectPlanService.countCompletedTasksForPersonByDateRanges(personName, firstDayOfMonth, lastDayOfMonth, currentDate);
            BigDecimal dailyProgress = BigDecimal.valueOf(completedCount)
                    .divide(BigDecimal.valueOf(totalTasksInMonth), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            dailyProgressList.add(new PersonnelMonthlyProgressResp.DailyProgress(currentDate, dailyProgress));
        }
        response.setLast10DaysProgress(dailyProgressList);

        return response;
    }

    /**
     * 获取个人周度未完成事项统计
     * @param personName 责任人姓名
     * @return 个人周度未完成事项统计
     */
    public PersonnelWeeklyUncompletedStatsResp getPersonnelWeeklyUncompletedStatsByPersonName(String personName) {
        PersonnelWeeklyUncompletedStatsResp response = new PersonnelWeeklyUncompletedStatsResp();
        LocalDate today = LocalDate.now();

        // 1. 上周未完成事项数
        LocalDate lastWeekStart = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastWeekEnd = lastWeekStart.plusDays(6);
        long lastWeekUncompletedCount = projectPlanService.countUncompletedTasksForWeek(personName, lastWeekStart, lastWeekEnd);
        response.setLastWeekUncompletedCount(lastWeekUncompletedCount);

        // 2. 与上上周比较
        LocalDate twoWeeksAgoStart = today.minusWeeks(2).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate twoWeeksAgoEnd = twoWeeksAgoStart.plusDays(6);
        long twoWeeksAgoUncompletedCount = projectPlanService.countUncompletedTasksForWeek(personName, twoWeeksAgoStart, twoWeeksAgoEnd);

        if (twoWeeksAgoUncompletedCount > 0) {
            BigDecimal change = BigDecimal.valueOf(lastWeekUncompletedCount - twoWeeksAgoUncompletedCount)
                    .divide(BigDecimal.valueOf(twoWeeksAgoUncompletedCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setChangePercentage(String.format("%+.2f%%", change));
        } else if (lastWeekUncompletedCount > 0) {
            response.setChangePercentage("+100.00%");
        } else {
            response.setChangePercentage("0.00%");
        }

        // 3. 本周之前的十周未完成事项数
        List<PersonnelWeeklyUncompletedStatsResp.WeeklyCount> weeklyCounts = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate currentWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int i = 10; i >= 1; i--) {
            LocalDate weekStartDate = currentWeekStart.minusWeeks(i);
            LocalDate weekEndDate = weekStartDate.plusDays(6);
            long count = projectPlanService.countUncompletedTasksForWeek(personName, weekStartDate, weekEndDate);

            String weekLabel = weekStartDate.getYear() + "-W" + weekStartDate.get(weekFields.weekOfWeekBasedYear());
            weeklyCounts.add(new PersonnelWeeklyUncompletedStatsResp.WeeklyCount(weekLabel, count));
        }
        response.setLast10WeeksUncompletedCounts(weeklyCounts);

        return response;
    }

    /**
     * 获取个人的待办事项列表
     * @param personName 责任人姓名
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 待办事项列表
     */
    public List<PersonnelTodoTaskResp> getPersonnelTodoTasks(String personName, LocalDate startDate, LocalDate endDate) {
        LocalDate queryStartDate = startDate;
        LocalDate queryEndDate = endDate;

        // 如果前端未提供日期，则默认查询本周
        if (queryStartDate == null || queryEndDate == null) {
            LocalDate today = LocalDate.now();
            queryStartDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            queryEndDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }

        return projectPlanService.findTodoTasksForPerson(personName, queryStartDate, queryEndDate);
    }

}