package com.zkyzn.project_manager.models.message;

import io.swagger.v3.oas.annotations.media.Schema; // 导入 Schema 注解
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * 周报/月报/年报消息内容
 * @author jiangsk
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用报告内容（周报/月报/年报）") // 类级别注解
public class ReportContent extends BaseContent {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Schema(description = "报告周期，例如 '2025-W23' (周), '2025-06' (月), '2025' (年)")
    private String reportPeriod; // 报告周期，可以是"YYYY-WW" (周) 或 "YYYY-MM" (月) 或 "YYYY" (年)

    @Schema(description = "报告类型，可选值: WEEKLY, MONTHLY, ANNUAL", example = "WEEKLY")
    private String reportType;   // 报告类型，例如 "WEEKLY", "MONTHLY", "ANNUAL"

    @Schema(description = "报告所属人姓名", example = "张三")
    private String personName;   // 报告所属人姓名

    @Schema(description = "本期已完成任务列表（周报/月报特有）")
    private List<TaskItem> completedTasks;

    @Schema(description = "本期未完成任务列表（周报/月报特有）")
    private List<TaskItem> uncompletedTasks;

    @Schema(description = "下期应完成任务列表（周报/月报特有）")
    private List<TaskItem> nextPeriodTasks;

    @Schema(description = "本期已完成任务总数")
    private Integer totalCompletedTasks;   // 本期已完成任务总数

    @Schema(description = "本期未完成任务总数")
    private Integer totalUncompletedTasks; // 本期未完成任务总数

    @Schema(description = "下期应完成任务总数")
    private Integer totalNextPeriodTasks;    // 下期应完成任务总数

    /**
     * 用于封装任务信息的内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "报告中的任务详情项") // 内部类级别注解
    public static class TaskItem {
        @Schema(description = "项目名称", example = "XX项目")
        private String projectName;

        @Schema(description = "任务包/所属阶段", example = "需求分析")
        private String taskPackage;

        @Schema(description = "任务描述", example = "完成用户登录模块设计")
        private String taskDescription;

        @Schema(description = "任务开始日期，格式：yyyy-MM-dd", example = "2025-06-01")
        private String startDate;

        @Schema(description = "任务结束日期/计划完成日期，格式：yyyy-MM-dd", example = "2025-06-15")
        private String endDate;

        @Schema(description = "任务状态", example = "COMPLETED")
        private String status;
    }

    /**
     * 构建周报/月报内容 (包含列表和汇总数量)
     */
    public static ReportContent from(
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            String type, // "WEEKLY", "MONTHLY"
            String personName,
            List<TaskItem> completedTasks,
            List<TaskItem> uncompletedTasks,
            List<TaskItem> nextPeriodTasks
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
            // 应该只在 WEEKLY 或 MONTHLY 类型下调用此方法
            content.setReportPeriod(periodStartDate.format(DATE_FORMATTER) + " ~ " + periodEndDate.format(DATE_FORMATTER));
        }

        content.setCompletedTasks(completedTasks);
        content.setUncompletedTasks(uncompletedTasks);
        content.setNextPeriodTasks(nextPeriodTasks);

        // 根据任务列表自动填充任务数量
        content.setTotalCompletedTasks(completedTasks != null ? completedTasks.size() : 0);
        content.setTotalUncompletedTasks(uncompletedTasks != null ? uncompletedTasks.size() : 0);
        content.setTotalNextPeriodTasks(nextPeriodTasks != null ? nextPeriodTasks.size() : 0);

        return content;
    }

    /**
     * 构建年度报告内容 (只包含汇总数量)
     */
    public static ReportContent fromAnnual(
            LocalDate reportYearDate, // 报告年份的任意一天，例如 Year.now().atDay(1)
            String personName,
            Integer totalCompletedTasks,
            Integer totalUncompletedTasks,
            Integer totalNextYearTasks // 这里的参数名可以保持 totalNextYearTasks，因为这是特定于 Annual 方法的
    ) {
        ReportContent content = new ReportContent();
        content.setReportType("ANNUAL");
        content.setPersonName(personName);
        content.setReportPeriod(String.valueOf(reportYearDate.getYear())); // 例如 "2025"

        content.setTotalCompletedTasks(totalCompletedTasks);
        content.setTotalUncompletedTasks(totalUncompletedTasks);
        content.setTotalNextPeriodTasks(totalNextYearTasks); // 设置到通用的 totalNextPeriodTasks 字段

        // 年度报告不包含具体的任务列表，所以这里设置为 null
        content.setCompletedTasks(null);
        content.uncompletedTasks = null;
        content.nextPeriodTasks = null;

        return content;
    }
}