package com.zkyzn.project_manager.crons;

import com.zkyzn.project_manager.constants.AppConstants;
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

import java.time.LocalDate;
import java.time.Year;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 年度报告定时任务
 * 每年1月1日上午9点发送年报给所有用户，包含上年度已完成、上年度未完成和本年度应完成的任务数量。
 */
@Component
public class AnnualReportCron {

    private static final Logger logger = LoggerFactory.getLogger(AnnualReportCron.class);

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ProjectPlanService projectPlanService;

    @Resource
    private MessageInfoStory messageInfoStory;

    /**
     * 每年1月1日上午9点执行，发送年度报告。
     * cron表达式：秒 分 时 天 月 周
     * "0 0 9 1 1 ?" 表示每年1月1日上午9点执行
     */
    @Scheduled(cron = "0 0 9 1 1 ?")
    public void sendAnnualReports() {
        logger.info("定时任务：开始发送年度报告...");

        List<UserInfo> allUsers = userInfoService.list();
        if (allUsers.isEmpty()) {
            logger.info("没有找到任何用户，跳过年度报告发送。");
            return;
        }

        LocalDate today = LocalDate.now();
        Year lastYear = Year.from(today).minusYears(1);
        LocalDate startOfLastYear = lastYear.atDay(1);
        LocalDate endOfLastYear = lastYear.atMonth(Month.DECEMBER).atEndOfMonth();

        Year thisYear = Year.from(today);
        LocalDate startOfThisYear = thisYear.atDay(1);
        LocalDate endOfThisYear = thisYear.atMonth(Month.DECEMBER).atEndOfMonth();

        for (UserInfo user : allUsers) {
            String responsiblePerson = user.getUserName(); // 获取责任人姓名

            // 1. 上年度已完成任务数量
            Integer totalCompletedTasksLastYear = projectPlanService.countPlansByDateRangeAndStatusForPerson(
                    responsiblePerson, startOfLastYear, endOfLastYear, TaskStatusEnum.COMPLETED.name());

            // 2. 上年度未完成任务数量 (在endDate范围内，但任务状态不是完成或中止)
            Integer totalUncompletedTasksLastYear = projectPlanService.countPlansByDateRangeAndUncompletedStatusForPerson(
                    responsiblePerson, startOfLastYear, endOfLastYear, TaskStatusEnum.COMPLETED.name(), TaskStatusEnum.STOP.name());

            // 3. 本年度应完成的任务数量
            Integer totalThisYearDueTasks = projectPlanService.countPlansByDateRangeForPerson(
                    responsiblePerson, startOfThisYear, endOfThisYear);

            ReportContent reportContent = ReportContent.fromAnnual(
                    startOfLastYear,
                    responsiblePerson, // 传入责任人姓名
                    totalCompletedTasksLastYear != null ? totalCompletedTasksLastYear : 0,
                    totalUncompletedTasksLastYear != null ? totalUncompletedTasksLastYear : 0,
                    totalThisYearDueTasks != null ? totalThisYearDueTasks : 0
            );

            MessageInfo message = MessageInfo.builder()
                    .senderId(AppConstants.SYSTEM_USER_ID)
                    .receiverId(user.getUserId())
                    .title("【年度报告】你的任务年报 (" + reportContent.getReportPeriod() + " 年度)")
                    .content(reportContent)
                    .messageType(6)
                    .readStatus(false)
                    .isTop(false)
                    .isReplyRequired(false)
                    .build();

            messageInfoStory.sendMessage(message);
            logger.info("已向用户 {} 发送年度报告。", responsiblePerson);
        }

        logger.info("定时任务：年度报告发送完成。");
    }
}