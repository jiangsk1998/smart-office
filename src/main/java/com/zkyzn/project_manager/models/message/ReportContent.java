package com.zkyzn.project_manager.models.message;

import com.zkyzn.project_manager.models.ProjectPlan; // 假设ProjectPlan模型已经存在且包含所需字段
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // 导入 DateTimeFormatter
import java.util.List;
import java.util.stream.Collectors;

/**
 * 周度报告消息内容
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReportContent extends BaseContent {

    // 使用 ISO_LOCAL_DATE 格式化日期，例如 "2025-06-23"
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private String reportDate; // 报告日期
    private String personName;    // 报告所属人姓名

    // 本周已完成任务列表
    private List<TaskItem> completedTasks;

    // 本周未完成任务列表
    private List<TaskItem> uncompletedTasks;

    // 下周应完成任务列表
    private List<TaskItem> nextTasks;

    // 用于封装任务信息的内部类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskItem {
        private String projectName;
        private String taskPackage;     // 任务包
        private String taskDescription; // 任务描述
        private String startDate;    // 任务开始日期
        private String endDate;      // 任务结束日期/计划完成日期
        private String status;          // 任务状态 (如果需要)
    }

    public static ReportContent from(
            LocalDate reportDate,
            String personName,
            List<ProjectPlan> completedPlans,
            List<ProjectPlan> uncompletedPlans,
            List<ProjectPlan> nextWeekPlans
    ) {
        ReportContent content = new ReportContent();
        content.setReportDate(reportDate.format(DATE_FORMATTER));
        content.setPersonName(personName);

        content.setCompletedTasks(completedPlans.stream()
                .map(plan -> TaskItem.builder()
                        .projectName(plan.getProjectName())
                        .taskPackage(plan.getTaskPackage())
                        .taskDescription(plan.getTaskDescription())
                        // 将 LocalDate 格式化为 String
                        .startDate(plan.getStartDate() != null ? plan.getStartDate().format(DATE_FORMATTER) : null)
                        .endDate(plan.getRealEndDate() != null ? plan.getRealEndDate().format(DATE_FORMATTER) : (plan.getEndDate() != null ? plan.getEndDate().format(DATE_FORMATTER) : null)) // 已完成任务优先使用实际结束日期
                        .build())
                .collect(Collectors.toList()));

        content.setUncompletedTasks(uncompletedPlans.stream()
                .map(plan -> TaskItem.builder()
                        .taskPackage(plan.getTaskPackage())
                        .taskDescription(plan.getTaskDescription())
                        // 将 LocalDate 格式化为 String
                        .endDate(plan.getEndDate() != null ? plan.getEndDate().format(DATE_FORMATTER) : null)
                        .build())
                .collect(Collectors.toList()));

        content.setNextTasks(nextWeekPlans.stream()
                .map(plan -> TaskItem.builder()
                        .taskPackage(plan.getTaskPackage())
                        .taskDescription(plan.getTaskDescription())
                        // 将 LocalDate 格式化为 String
                        .endDate(plan.getEndDate() != null ? plan.getEndDate().format(DATE_FORMATTER) : null)
                        .build())
                .collect(Collectors.toList()));

        return content;
    }
}