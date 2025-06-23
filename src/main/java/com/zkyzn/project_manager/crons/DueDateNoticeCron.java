package com.zkyzn.project_manager.crons;

import com.zkyzn.project_manager.enums.ProjectStatusEnum;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.models.UserInfo;
import com.zkyzn.project_manager.models.message.DueDateNoticeContent;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.services.UserInfoService;
import com.zkyzn.project_manager.stories.MessageInfoStory;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * T+1 到期通知定时任务
 * 每天检查明天到期的项目和任务，如果未完成则发送即将到期通知给相关负责人。
 */
@Component
public class DueDateNoticeCron {

    private static final Logger logger = LoggerFactory.getLogger(DueDateNoticeCron.class);

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private ProjectPlanService projectPlanService;

    @Resource
    private MessageInfoStory messageInfoStory;

    @Resource
    private UserInfoService userInfoService;

    private static final Long SYSTEM_USER_ID = 1L;

    /**
     * 每天凌晨1点执行，检查明天到期的项目和任务
     * cron表达式：秒 分 时 天 月 周
     * "0 0 1 * * ?" 表示每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkAndSendDueDateNotices() {
        logger.info("定时任务：开始检查明天到期未完成的项目和任务，发送即将到期通知...");
        LocalDate tomorrow = LocalDate.now().plusDays(1); // 检查明天到期的任务和项目
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 1. 检查项目即将到期
//        checkDueProjects(tomorrow, dateFormatter);

        // 2. 检查任务即将到期
        checkDueTasks(tomorrow, dateFormatter);

        logger.info("定时任务：即将到期通知检查完成。");
    }

    /**
     * 检查并发送即将到期项目通知
     *
     * @param dueDate   截止日期 (明天)
     * @param formatter 日期格式化器
     */
    private void checkDueProjects(LocalDate dueDate, DateTimeFormatter formatter) {
        // 查询所有明天到期但未完成（非COMPLETED状态）的项目
        List<ProjectInfo> dueProjects = projectInfoService.getDelayedProjects(dueDate, ProjectStatusEnum.COMPLETED.name());

        for (ProjectInfo project : dueProjects) {
            // 构建通知内容
            DueDateNoticeContent content = new DueDateNoticeContent();
            content.setProjectNumber(project.getProjectNumber());
            content.setProjectName(project.getProjectName());
            content.setStartDate(project.getStartDate().format(formatter));
            content.setEndDate(project.getEndDate().format(formatter));
            content.setCurrentPhase(project.getCurrentPhase());
            content.setContent("项目即将到期，请尽快处理。");

            // 构建消息
            MessageInfo message = MessageInfo.builder()
                    .senderId(SYSTEM_USER_ID)
                    .title("项目即将到期通知: " + project.getProjectName())
                    .content(content)
                    .messageType(2) // 消息类型：2=即将到期通知
                    .readStatus(false)
                    .isTop(true) // 即将到期通知通常是重要信息，可以置顶
                    .isReplyRequired(false) // 通常不需要回复
                    .build();

            // 发送给项目负责人和技术负责人
            Set<Long> recipients = Sets.newHashSet();
            Optional.ofNullable(project.getResponsibleLeaderId()).ifPresent(recipients::add);
            Optional.ofNullable(project.getTechnicalLeaderId()).ifPresent(recipients::add);
            // 将计划主管加入通知人
            if (project.getPlanSupervisors() != null) {
                project.getPlanSupervisors().forEach(user -> recipients.add(user.getUserId()));
            }

            if (!recipients.isEmpty()) {
                messageInfoStory.sendMessages(message, recipients);
            }
        }
    }

    /**
     * 检查并发送即将到期任务通知
     *
     * @param dueDate   截止日期 (明天)
     * @param formatter 日期格式化器
     */
    private void checkDueTasks(LocalDate dueDate, DateTimeFormatter formatter) {
        // 查询所有明天到期但未完成（非COMPLETED和STOP状态）的任务
        List<ProjectPlan> dueTasks = projectPlanService.getDelayedTasks(dueDate, TaskStatusEnum.COMPLETED.name(), TaskStatusEnum.STOP.name());

        for (ProjectPlan task : dueTasks) {
            // 构建通知内容
            DueDateNoticeContent content = new DueDateNoticeContent();
            content.setProjectNumber(getProjectNumberForTask(task.getProjectId())); // 获取项目工号
            content.setProjectName(getProjectNameForTask(task.getProjectId())); // 获取项目名称
            content.setStartDate(task.getStartDate().format(formatter));
            content.setEndDate(task.getEndDate().format(formatter));
            content.setCurrentPhase(task.getTaskPackage()); // 任务的“任务包”通常对应阶段名称
            content.setContent("任务即将到期，请尽快处理。");

            // 构建消息
            MessageInfo message = MessageInfo.builder()
                    .senderId(SYSTEM_USER_ID)
                    .title("任务即将到期通知: " + task.getTaskDescription())
                    .content(content)
                    .messageType(2) // 消息类型：2=即将到期通知
                    .readStatus(false)
                    .isTop(true) // 即将到期通知通常是重要信息，可以置顶
                    .isReplyRequired(false) // 任务到期通知通常不需要回复
                    .build();

            // 发送给任务责任人
            UserInfo userInfo = userInfoService.getUserInfoByName(task.getResponsiblePerson());
            if (userInfo != null) {
                message.setReceiverId(userInfo.getUserId());
                messageInfoStory.sendMessage(message);
            }

            // 同时发送给项目技术负责人
            ProjectInfo associatedProject = projectInfoService.getByProjectId(task.getProjectId());
            Set<Long> recipients = Sets.newHashSet();
            if (associatedProject != null) {
                Optional.ofNullable(associatedProject.getTechnicalLeaderId()).ifPresent(recipients::add);
            }
            if (!recipients.isEmpty()) {
                message.setIsReplyRequired(false); // 对这些负责人不需要回复
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