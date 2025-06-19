package com.zkyzn.project_manager.stories;


import com.zkyzn.project_manager.enums.TaskStatusEnum;
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

    /**
     * 根据项目ID、类型和日期获取进度率
     * @param projectId
     * @param type
     * @param date
     * @return
     */
    public BigDecimal getProgressRate(Long projectId, String type, LocalDate date) {
        // 根据type参数的不同，调用不同的方法计算进度率
        return switch (type) {
            case "overall" -> calculateOverallProgress(projectId, date);
            case "monthly" -> calculateMonthlyProgress(projectId, date);
            case "weekly" -> calculateWeeklyProgress(projectId, date);
            // 默认返回0
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal calculateOverallProgress(Long projectId, LocalDate date) {
        long total = projectPlanService.countByProjectId(projectId);
        if (total == 0) {
            return BigDecimal.ZERO;
        }

        long completed = projectPlanService.countByProjectIdTaskStatusAndRealEndDate(projectId, TaskStatusEnum.COMPLETED.name(), date);

        return BigDecimal.valueOf(completed)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMonthlyProgress(Long projectId, LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        long total = projectPlanService.countByDateRange(projectId, firstDay, lastDay);
        if (total == 0) {
            return BigDecimal.ZERO;
        }

        long completed = projectPlanService.countCompletedByDateRange(projectId, firstDay, lastDay);
        return BigDecimal.valueOf(completed)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateWeeklyProgress(Long projectId, LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        long total = projectPlanService.countByDateRange(projectId, startOfWeek, endOfWeek);
        if (total == 0) {
            return BigDecimal.ZERO;
        }

        long completed = projectPlanService.countCompletedByDateRange(projectId, startOfWeek, date);
        return BigDecimal.valueOf(completed)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }
}
