package com.zkyzn.project_manager.crons;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入LambdaQueryWrapper
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo; // 导入 ProjectInfo
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.models.UserInfo;
import com.zkyzn.project_manager.models.message.ReportContent;
import com.zkyzn.project_manager.models.message.ReportContent.TaskItem; // 导入 TaskItem 内部类
import com.zkyzn.project_manager.services.ProjectInfoService; // 导入 ProjectInfoService
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.services.UserInfoService;
import com.zkyzn.project_manager.stories.MessageInfoStory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // 导入 DateTimeFormatter
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 每周报告定时任务
 * 每周五发送周报给所有用户，包含本周已完成、本周未完成和下周应完成的任务列表。
 */
@Component
public class WeeklyReportCron {

    private static final Logger logger = LoggerFactory.getLogger(WeeklyReportCron.class);
    private static final Long SYSTEM_USER_ID = 1L; // 假设系统发送通知的用户ID
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // 定义日期格式化器

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ProjectPlanService projectPlanService;

    @Resource
    private MessageInfoStory messageInfoStory;

    @Resource
    private ProjectInfoService projectInfoService; // 注入 ProjectInfoService

    /**
     * 每周五上午9点执行，发送每周报告。
     * cron表达式：秒 分 时 天 月 周
     * "0 0 9 ? * FRI" 表示每周五上午9点执行
     */
    @Scheduled(cron = "0 0 9 ? * FRI")
    public void sendWeeklyReports() {
        logger.info("定时任务：开始发送每周报告...");

        List<UserInfo> allUsers = userInfoService.list();
        if (allUsers.isEmpty()) {
            logger.info("没有找到任何用户，跳过每周报告发送。");
            return;
        }

        // 1. 一次性获取所有项目信息，并构建一个Map用于快速查找
        List<ProjectInfo> allProjects = projectInfoService.list();
        Map<Long, String> projectIdToNameMap = allProjects.stream()
                .collect(Collectors.toMap(ProjectInfo::getProjectId, ProjectInfo::getProjectName));

        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfThisWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate startOfNextWeek = endOfThisWeek.plusDays(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfNextWeek = startOfNextWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (UserInfo user : allUsers) {
            String personName = user.getUserName();

            // 1. 本周已完成任务列表
            List<ProjectPlan> completedTasksThisWeek = projectPlanService.getPlansByDateRangeAndStatusForPerson(
                    personName, startOfThisWeek, endOfThisWeek, TaskStatusEnum.COMPLETED.name());

            // 2. 本周未完成任务列表
            List<ProjectPlan> uncompletedTasksThisWeek = projectPlanService.getPlansByDateRangeAndUncompletedStatusForPerson(
                    personName, startOfThisWeek, endOfThisWeek, TaskStatusEnum.COMPLETED.name(), TaskStatusEnum.STOP.name());

            // 3. 下周应完成的任务列表
            List<ProjectPlan> nextWeekDueTasks = projectPlanService.getPlansByDateRangeForPerson(
                    personName, startOfNextWeek, endOfNextWeek);

            // 构建 ReportContent 时，在这里关联项目名称
            ReportContent reportContent = new ReportContent();
            reportContent.setReportDate(today.format(DATE_FORMATTER));
            reportContent.setPersonName(personName);

            // 转换 ProjectPlan 为 TaskItem 并填充 projectName
            reportContent.setCompletedTasks(mapProjectPlansToTaskItems(completedTasksThisWeek, projectIdToNameMap));
            reportContent.setUncompletedTasks(mapProjectPlansToTaskItems(uncompletedTasksThisWeek, projectIdToNameMap));
            reportContent.setNextTasks(mapProjectPlansToTaskItems(nextWeekDueTasks, projectIdToNameMap));

            MessageInfo message = MessageInfo.builder()
                    .senderId(SYSTEM_USER_ID)
                    .receiverId(user.getUserId())
                    .title("【每周报告】你的任务周报 (" + today.format(dateFormatter) + ")")
                    .content(reportContent)
                    .messageType(0)
                    .readStatus(false)
                    .isTop(false)
                    .isReplyRequired(false)
                    .build();

            messageInfoStory.sendMessage(message);
            logger.info("已向用户 {} 发送每周报告。", personName);
        }

        logger.info("定时任务：每周报告发送完成。");
    }

    /**
     * 将 ProjectPlan 列表转换为 TaskItem 列表，并根据 projectId 填充 projectName。
     * @param projectPlans ProjectPlan 列表
     * @param projectIdToNameMap 项目ID到名称的映射
     * @return TaskItem 列表
     */
    private List<TaskItem> mapProjectPlansToTaskItems(List<ProjectPlan> projectPlans, Map<Long, String> projectIdToNameMap) {
        return projectPlans.stream()
                .map(plan -> TaskItem.builder()
                        .projectName(projectIdToNameMap.getOrDefault(plan.getProjectId(), "未知项目")) // 根据ID获取名称
                        .taskPackage(plan.getTaskPackage())
                        .taskDescription(plan.getTaskDescription())
                        // 将 LocalDate 格式化为 String
                        .startDate(plan.getStartDate() != null ? plan.getStartDate().format(DATE_FORMATTER) : null)
                        .endDate(plan.getRealEndDate() != null ? plan.getRealEndDate().format(DATE_FORMATTER) : (plan.getEndDate() != null ? plan.getEndDate().format(DATE_FORMATTER) : null))
                        .build())
                .collect(Collectors.toList());
    }
}