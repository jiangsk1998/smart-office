package com.zkyzn.project_manager.crons;

import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.models.UserInfo;
import com.zkyzn.project_manager.models.message.ReportContent;
import com.zkyzn.project_manager.models.message.ReportContent.TaskItem;
import com.zkyzn.project_manager.services.ProjectInfoService;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 周度报告定时任务
 * 每周一上午9点发送周报给所有用户
 */
@Component
public class WeeklyReportCron {

    private static final Logger logger = LoggerFactory.getLogger(WeeklyReportCron.class);
    private static final Long SYSTEM_USER_ID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ProjectPlanService projectPlanService;

    @Resource
    private MessageInfoStory messageInfoStory;

    @Resource
    private ProjectInfoService projectInfoService;

    /**
     * 每周一上午9点执行，发送周度报告。
     * cron表达式：秒 分 时 天 月 周
     * "0 0 9 ? * MON" 表示每周一上午9点执行
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    public void sendWeeklyReports() {
        logger.info("定时任务：开始发送周度报告...");

        List<UserInfo> allUsers = userInfoService.list();
        if (allUsers.isEmpty()) {
            logger.info("没有找到任何用户，跳过周度报告发送。");
            return;
        }

        List<ProjectInfo> allProjects = projectInfoService.list();
        Map<Long, String> projectIdToNameMap = allProjects.stream()
                .collect(Collectors.toMap(ProjectInfo::getProjectId, ProjectInfo::getProjectName));


        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate lastSunday = lastMonday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDate thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisSunday = thisMonday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));


        for (UserInfo user : allUsers) {
            String responsiblePerson = user.getUserName(); // 获取责任人姓名

            List<ProjectPlan> completedPlans = projectPlanService.getPlansByDateRangeAndStatusForPerson(
                    responsiblePerson, lastMonday, lastSunday, TaskStatusEnum.COMPLETED.name());

            List<ProjectPlan> uncompletedPlans = projectPlanService.getPlansByDateRangeAndUncompletedStatusForPerson(
                    responsiblePerson, lastMonday, lastSunday, TaskStatusEnum.COMPLETED.name(), TaskStatusEnum.STOP.name());

            List<ProjectPlan> thisWeekDuePlans = projectPlanService.getPlansByDateRangeForPerson(
                    responsiblePerson, thisMonday, thisSunday);

            List<TaskItem> completedTasks = mapProjectPlansToTaskItems(completedPlans, projectIdToNameMap);
            List<TaskItem> uncompletedTasks = mapProjectPlansToTaskItems(uncompletedPlans, projectIdToNameMap);
            List<TaskItem> thisWeekDueTasks = mapProjectPlansToTaskItems(thisWeekDuePlans, projectIdToNameMap);

            ReportContent reportContent = ReportContent.from(
                    lastMonday,
                    lastSunday,
                    "WEEKLY",
                    responsiblePerson, // 传入责任人姓名
                    completedTasks,
                    uncompletedTasks,
                    thisWeekDueTasks
            );

            MessageInfo message = MessageInfo.builder()
                    .senderId(SYSTEM_USER_ID)
                    .receiverId(user.getUserId())
                    .title("【周度报告】你的任务周报 (" + reportContent.getReportPeriod() + ")")
                    .content(reportContent)
                    .messageType(6)
                    .readStatus(false)
                    .isTop(false)
                    .isReplyRequired(false)
                    .build();

            messageInfoStory.sendMessage(message);
            logger.info("已向用户 {} 发送周度报告。", responsiblePerson);
        }

        logger.info("定时任务：周度报告发送完成。");
    }

    private List<TaskItem> mapProjectPlansToTaskItems(List<ProjectPlan> projectPlans, Map<Long, String> projectIdToNameMap) {
        return projectPlans.stream()
                .map(plan -> TaskItem.builder()
                        .projectName(projectIdToNameMap.getOrDefault(plan.getProjectId(), "未知项目"))
                        .taskPackage(plan.getTaskPackage())
                        .taskDescription(plan.getTaskDescription())
                        .startDate(plan.getStartDate() != null ? plan.getStartDate().format(DATE_FORMATTER) : null)
                        .endDate(plan.getRealEndDate() != null ? plan.getRealEndDate().format(DATE_FORMATTER) : (plan.getEndDate() != null ? plan.getEndDate().format(DATE_FORMATTER) : null))
                        .status(plan.getTaskStatus())
                        .build())
                .collect(Collectors.toList());
    }
}