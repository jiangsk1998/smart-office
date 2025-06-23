package com.zkyzn.project_manager.models.message;

// import com.zkyzn.project_manager.models.ProjectPlan; // 不再直接引用 ProjectPlan
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
// import java.util.stream.Collectors; // 如果不使用 stream 转换 ProjectPlan，则不需要

/**
 * 周报/月报消息内容
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReportContent extends BaseContent {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-dd

    private String reportPeriod; // 报告周期，可以是"YYYY-WW" (周) 或 "YYYY-MM" (月)
    private String reportType;   // 报告类型，例如 "WEEKLY" 或 "MONTHLY"
    private String personName;   // 报告所属人姓名

    // 本期已完成任务列表
    private List<TaskItem> completedTasks;

    // 本期未完成任务列表 (计划本期完成但未完成的)
    private List<TaskItem> uncompletedTasks;

    // 下期应完成任务列表 (计划下期完成的)
    private List<TaskItem> nextPeriodTasks;

    // 用于封装任务信息的内部类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskItem {
        private String projectName;     // 项目名称
        private String taskPackage;     // 任务包
        private String taskDescription; // 任务描述
        private String startDate;       // 任务开始日期
        private String endDate;         // 任务结束日期/计划完成日期
        private String status;          // 任务状态 (如果需要，例如对于未完成任务)
    }

    /**
     * 构建周报/月报内容
     * @param periodStartDate 报告周期的开始日期 (例如周一或月第一天)
     * @param periodEndDate   报告周期的结束日期 (例如周日或月最后一天)
     * @param type            报告类型 ("WEEKLY" 或 "MONTHLY")
     * @param personName      报告所属人姓名
     * @param completedTasks  本期已完成任务项列表 (TaskItem)
     * @param uncompletedTasks 本期未完成任务项列表 (TaskItem)
     * @param nextPeriodTasks 下期应完成任务项列表 (TaskItem)
     * @return ReportContent 实例
     */
    public static ReportContent from(
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            String type, // "WEEKLY" or "MONTHLY"
            String personName,
            List<TaskItem> completedTasks,  // <--- 修改为 List<TaskItem>
            List<TaskItem> uncompletedTasks, // <--- 修改为 List<TaskItem>
            List<TaskItem> nextPeriodTasks   // <--- 修改为 List<TaskItem>
    ) {
        ReportContent content = new ReportContent();
        content.setReportType(type);
        content.setPersonName(personName);

        if ("WEEKLY".equalsIgnoreCase(type)) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int weekOfYear = periodStartDate.get(weekFields.weekOfWeekBasedYear());
            int year = periodStartDate.get(weekFields.weekBasedYear());
            content.setReportPeriod(String.format("%d-W%02d", year, weekOfYear));
        } else if ("MONTHLY".equalsIgnoreCase(type)) {
            content.setReportPeriod(periodStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        } else {
            content.setReportPeriod(periodStartDate.format(DATE_FORMATTER) + " ~ " + periodEndDate.format(DATE_FORMATTER));
        }

        // 直接设置已转换好的 TaskItem 列表
        content.setCompletedTasks(completedTasks);
        content.setUncompletedTasks(uncompletedTasks);
        content.setNextPeriodTasks(nextPeriodTasks);

        return content;
    }
}