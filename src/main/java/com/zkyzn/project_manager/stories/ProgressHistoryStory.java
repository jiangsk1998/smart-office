package com.zkyzn.project_manager.stories;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ProjectPlanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

/**
 * @author: Mr-ti
 * Date: 2025/6/12 17:37
 */
@Service
public class ProgressHistoryStory {

    @Resource
    private ProjectPlanService projectPlanService;

    public BigDecimal getProgressRate(Long projectId, String type, LocalDate date) {
        switch (type) {
            case "overall":
                return calculateOverallProgress(projectId, date);
            case "monthly":
                return calculateMonthlyProgress(projectId, date);
            case "weekly":
                return calculateWeeklyProgress(projectId, date);
            case "delayed":
                return calculateDelayedProgress(projectId, date);
            default:
                return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateOverallProgress(Long projectId, LocalDate date) {
        Long total = projectPlanService.countByProjectId(projectId);
        if (total == 0) return BigDecimal.ZERO;

        // 查询截止到该日期的完成数
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("task_status", "已完成")
                .le("real_end_date", date);
        Long completed = projectPlanService.count(wrapper);

        return BigDecimal.valueOf(completed)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMonthlyProgress(Long projectId, LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        Long total = projectPlanService.countByDateRange(projectId, firstDay, lastDay);
        if (total == 0) return BigDecimal.ZERO;

        Long completed = projectPlanService.countCompletedByDateRange(projectId, firstDay, lastDay);
        return BigDecimal.valueOf(completed)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateWeeklyProgress(Long projectId, LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Long total = projectPlanService.countByDateRange(projectId, startOfWeek, endOfWeek);
        if (total == 0) return BigDecimal.ZERO;

        Long completed = projectPlanService.countCompletedByDateRange(projectId, startOfWeek, endOfWeek);
        return BigDecimal.valueOf(completed)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDelayedProgress(Long projectId, LocalDate date) {
        QueryWrapper<ProjectPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .ne("task_status", "已完成")
                .lt("end_date", date);
        Long delayed = projectPlanService.count(wrapper);

        return BigDecimal.valueOf(delayed);
    }
}
