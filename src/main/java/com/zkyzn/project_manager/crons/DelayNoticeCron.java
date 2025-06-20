package com.zkyzn.project_manager.crons;

import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.models.UserInfo;
import com.zkyzn.project_manager.models.message.DelayNoticeContent;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.services.UserInfoService;
import com.zkyzn.project_manager.stories.MessageInfoStory;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.Sets;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 延期通知定时任务
 * 每天检查前一天到期的项目和任务，如果未完成则发送延期通知给相关负责人。
 */
@Component
public class DelayNoticeCron {

    private static final Logger logger = LoggerFactory.getLogger(DelayNoticeCron.class);

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private ProjectPlanService projectPlanService;

    @Resource
    private MessageInfoStory messageInfoStory;

    @Resource
    private UserInfoService userInfoService;

    private static final Long SYSTEM_USER_ID = 1L; // 假设系统发送通知的用户ID


    /**
     * 每天凌晨2点执行，检查前一天到期的项目和任务
     * cron表达式：秒 分 时 天 月 周
     * "0 0 2 * * ?" 表示每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkAndSendDelayNotices() {
        logger.info("定时任务：开始检查到期未完成的项目和任务，发送延期通知...");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 2. 检查任务延期
        checkDelayedTasks(yesterday, dateFormatter);

        logger.info("定时任务：延期通知检查完成。");
    }

    /**
     * 检查并发送延期任务通知
     *
     * @param dueDate   截止日期 (昨天)
     * @param formatter 日期格式化器
     */
    private void checkDelayedTasks(LocalDate dueDate, DateTimeFormatter formatter) {
        // 查询所有昨天到期但未完成（非COMPLETED和STOP状态）的任务
        List<ProjectPlan> delayedTasks = projectPlanService.getDelayedTasks(dueDate, TaskStatusEnum.COMPLETED.name(), TaskStatusEnum.STOP.name());

        for (ProjectPlan task : delayedTasks) {
            // 构建通知内容
            DelayNoticeContent content = new DelayNoticeContent();
            content.setProjectNumber(getProjectNumberForTask(task.getProjectId())); // 获取项目工号
            content.setProjectName(getProjectNameForTask(task.getProjectId())); // 获取项目名称
            content.setStartDate(task.getStartDate().format(formatter));
            content.setEndDate(task.getEndDate().format(formatter));
            content.setCurrentPhase(task.getTaskPackage()); // 任务的“任务包”通常对应阶段名称

            // 构建消息
            MessageInfo message = MessageInfo.builder()
                    .senderId(SYSTEM_USER_ID)
                    .title("任务延期通知: " + task.getTaskDescription())
                    .content(content)
                    .messageType(3) // 消息类型：3=延期通知
                    .readStatus(false)
                    .isTop(true) // 延期通知通常是重要信息，可以置顶
                    .isReplyRequired(true)
                    .build();

            UserInfo userInfo = userInfoService.getUserInfoByName(task.getResponsiblePerson());
            if (userInfo != null) {
                message.setReceiverId(userInfo.getUserId());
                messageInfoStory.sendMessage(message);
            }

            ProjectInfo associatedProject = projectInfoService.getByProjectId(task.getProjectId());
            Set<Long> recipients = Sets.newHashSet();
            if (associatedProject != null) {
                Optional.ofNullable(associatedProject.getResponsibleLeaderId()).ifPresent(recipients::add);
                Optional.ofNullable(associatedProject.getTechnicalLeaderId()).ifPresent(recipients::add);
            }
            if (!recipients.isEmpty()) {
                message.setIsReplyRequired(false);
                messageInfoStory.sendMessages(message, recipients);
            }
        }
    }

    /**
     * 根据项目ID获取项目工号
     *
     * @param projectId 项目ID
     * @return 项目工号
     */
    private String getProjectNumberForTask(Long projectId) {
        ProjectInfo projectInfo = projectInfoService.getByProjectId(projectId);
        return projectInfo != null ? projectInfo.getProjectNumber() : "未知项目工号";
    }

    /**
     * 根据项目ID获取项目名称
     *
     * @param projectId 项目ID
     * @return 项目名称
     */
    private String getProjectNameForTask(Long projectId) {
        ProjectInfo projectInfo = projectInfoService.getByProjectId(projectId);
        return projectInfo != null ? projectInfo.getProjectName() : "未知项目";
    }
}