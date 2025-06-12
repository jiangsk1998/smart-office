package com.zkyzn.project_manager.listener;

import com.zkyzn.project_manager.events.ProjectPhaseChangeEvent;
import com.zkyzn.project_manager.events.ProjectTaskChangeEvent;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.models.message.ChangeNoticeContent;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.stories.MessageInfoStory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class ProjectActivityListener {

    @Resource
    private MessageInfoStory messageInfoStory;
    @Resource
    private ProjectInfoService projectInfoService;

    // 监听项目阶段变更事件
    @EventListener
    public void handlePhaseChangeEvent(ProjectPhaseChangeEvent event) {
        Long operatorId = event.getOperatorId();
        ProjectPhase phase = event.getUpdatedPhase() != null ? event.getUpdatedPhase() : event.getOriginalPhase();
        if (phase == null) return;

        ProjectInfo projectInfo = projectInfoService.getByProjectId(phase.getProjectId());
        if (projectInfo == null) return;

        String title = "";
        String contentText = "";

        switch (event.getChangeType()) {
            case "CREATE":
                title = "新增项目阶段通知";
                contentText = buildCreateNoticeContent(phase);
                break;
            case "UPDATE":
                title = "项目阶段信息更新通知";
                contentText = buildUpdateNoticeContent(event.getOriginalPhase(), event.getUpdatedPhase());
                break;
            case "DELETE":
                title = "项目阶段删除通知";
                contentText = String.format("项目阶段 [%s] 已被永久删除", phase.getPhaseName());
                break;
            case "STATUS_CHANGE":
                 title = "项目阶段状态变更通知";
                 contentText = String.format("项目阶段状态已从 [%s] 更新为: [%s]",
                         event.getOriginalPhase().getPhaseStatus(), event.getUpdatedPhase().getPhaseStatus());
                 break;
        }
        
        if (StringUtils.isBlank(title)) return;

        // 发送通知
        sendNotification(projectInfo, title, contentText, operatorId, phase);
    }


    
    private void sendNotification(ProjectInfo projectInfo, String title, String contentText, Long operatorId, ProjectPhase phase) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSenderId(operatorId);
        messageInfo.setTitle(title);
        messageInfo.setMessageType(1); // 1 代表变更通知
        messageInfo.setReadStatus(false);

        // 构建通知内容
        ChangeNoticeContent noticeContent = new ChangeNoticeContent();
        noticeContent.setProjectNumber(projectInfo.getProjectNumber());
        noticeContent.setProjectName(projectInfo.getProjectName());
        noticeContent.setCurrentPhase(phase.getPhaseName());
        noticeContent.setStartDate(phase.getStartDate().toString());
        noticeContent.setEndDate(phase.getEndDate().toString());
        noticeContent.setContent(contentText);
        messageInfo.setContent(noticeContent);

        // 获取接收人并发送
        Set<Long> recipients = getNotifyRecipients(projectInfo);
        if (!recipients.isEmpty()) {
            messageInfoStory.sendMessages(messageInfo, recipients);
        }
    }

    private Set<Long> getNotifyRecipients(ProjectInfo projectInfo) {
        // TODO 获取项目负责人、技术负责人、计划负责人
        Set<Long> recipients = new HashSet<>(3);
        Optional.ofNullable(projectInfo.getResponsibleLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getTechnicalLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getPlanSupervisorId()).ifPresent(recipients::add);
        return Set.of(projectInfo.getResponsibleLeaderId(), projectInfo.getTechnicalLeaderId(), projectInfo.getPlanSupervisorId());
    }

    private String buildCreateNoticeContent(ProjectPhase phase) {
        return String.format("新增项目阶段 [%s]：\n- 负责人: %s\n- 计划时间: %s 至 %s\n- 交付物: %s",
                formatValue(phase.getPhaseName()),
                formatValue(phase.getResponsiblePerson()),
                formatDate(phase.getStartDate()),
                formatDate(phase.getEndDate()),
                formatValue(phase.getDeliverable()));
    }

    private String buildUpdateNoticeContent(ProjectPhase original, ProjectPhase updated) {
        StringBuilder changes = new StringBuilder("项目阶段信息更新：\n");
        addChangeIfDifferent(changes, "阶段名称", original.getPhaseName(), updated.getPhaseName());
        addDateChangeIfDifferent(changes, "开始日期", original.getStartDate(), updated.getStartDate());
        addDateChangeIfDifferent(changes, "结束日期", original.getEndDate(), updated.getEndDate());
        addChangeIfDifferent(changes, "负责人", original.getResponsiblePerson(), updated.getResponsiblePerson());
        //... 其他字段比较
        return changes.length() > "项目阶段信息更新：\n".length() ? changes.toString() : "无字段变更";
    }

    private String formatValue(String value) {
        return StringUtils.isNotBlank(value) ? value : "未设置";
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : "未指定";
    }
    
    private void addChangeIfDifferent(StringBuilder sb, String fieldName, String oldVal, String newVal) {
        if (!Objects.equals(oldVal, newVal)) {
            sb.append(String.format("- %s: [%s] → [%s]\n", fieldName, formatValue(oldVal), formatValue(newVal)));
        }
    }

    private void addDateChangeIfDifferent(StringBuilder sb, String fieldName, LocalDate oldDate, LocalDate newDate) {
        if (!Objects.equals(oldDate, newDate)) {
            sb.append(String.format("- %s: [%s] → [%s]\n", fieldName, formatDate(oldDate), formatDate(newDate)));
        }
    }


    /**
     * 监听并处理项目任务的变更事件。
     * 当 ProjectTaskStory 中发布一个 ProjectTaskChangeEvent 时，此方法会被自动调用。
     *
     * @param event 包含任务变更详细信息的事件对象。
     */
    @EventListener
    public void handleTaskChangeEvent(ProjectTaskChangeEvent event) {
        Long operatorId = event.getOperatorId();
        ProjectPlan plan = event.getUpdatedPlan() != null ? event.getUpdatedPlan() : event.getOriginalPlan();
        if (plan == null) return;

        ProjectInfo projectInfo = projectInfoService.getByProjectId(plan.getProjectId());
        if (projectInfo == null) return;

        String title = "";
        String contentText = "";

        switch (event.getChangeType()) {
            case "CREATE":
                title = "新增项目任务通知";
                contentText = buildCreateTaskNoticeContent(plan);
                break;
            case "UPDATE":
                title = "项目任务信息更新通知";
                contentText = buildUpdateTaskNoticeContent(event.getOriginalPlan(), event.getUpdatedPlan());
                break;
            case "DELETE":
                title = "项目任务删除通知";
                contentText = String.format("任务 [%s] 已被删除", plan.getTaskDescription());
                break;
            case "STATUS_CHANGE":
                title = "项目任务状态变更通知";
                contentText = String.format("任务状态已从 [%s] 更新为: [%s]",
                        event.getOriginalPlan().getTaskStatus(), event.getUpdatedPlan().getTaskStatus());
                break;
        }

        if (StringUtils.isBlank(title)) return;

        // 复用发送通知的逻辑，但需要为任务定制一个发送方法或改造现有方法
        sendTaskNotification(projectInfo, title, contentText, operatorId, plan);
    }

    /**
     * 发送与项目任务相关的通知。
     * @param projectInfo 关联的项目信息
     * @param title 通知标题
     * @param contentText 通知主体内容
     * @param operatorId 操作员ID
     * @param plan 相关的任务计划对象
     */
    private void sendTaskNotification(ProjectInfo projectInfo, String title, String contentText, Long operatorId, ProjectPlan plan) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSenderId(operatorId);
        messageInfo.setTitle(title);
        messageInfo.setMessageType(1); // 1 代表变更通知
        messageInfo.setReadStatus(false);

        ChangeNoticeContent noticeContent = new ChangeNoticeContent();
        noticeContent.setProjectNumber(projectInfo.getProjectNumber());
        noticeContent.setProjectName(projectInfo.getProjectName());
        noticeContent.setCurrentPhase(plan.getTaskPackage()); // 任务所属的任务包作为当前阶段
        noticeContent.setStartDate(plan.getStartDate().toString());
        noticeContent.setEndDate(plan.getEndDate().toString());
        noticeContent.setContent(contentText);
        messageInfo.setContent(noticeContent);

        Set<Long> recipients = getNotifyRecipients(projectInfo);
        if (!recipients.isEmpty()) {
            messageInfoStory.sendMessages(messageInfo, recipients);
        }
    }

    // --- 新增的任务通知内容构建方法 ---

    private String buildCreateTaskNoticeContent(ProjectPlan plan) {
        return String.format("新增任务: %s\n- 责任人: %s\n- 计划时间: %s 至 %s\n- 交付物: %s",
                formatValue(plan.getTaskDescription()),
                formatValue(plan.getResponsiblePerson()),
                formatDate(plan.getStartDate()),
                formatDate(plan.getEndDate()),
                formatValue(plan.getDeliverable()));
    }

    private String buildUpdateTaskNoticeContent(ProjectPlan original, ProjectPlan updated) {
        StringBuilder changes = new StringBuilder("任务变更内容:\n");
        addChangeIfDifferent(changes, "任务描述", original.getTaskDescription(), updated.getTaskDescription());
        addChangeIfDifferent(changes, "责任人", original.getResponsiblePerson(), updated.getResponsiblePerson());
        addDateChangeIfDifferent(changes, "开始日期", original.getStartDate(), updated.getStartDate());
        addDateChangeIfDifferent(changes, "结束日期", original.getEndDate(), updated.getEndDate());
        addChangeIfDifferent(changes, "交付物", original.getDeliverable(), updated.getDeliverable());
        addChangeIfDifferent(changes, "科室", original.getDepartment(), updated.getDepartment());
        return changes.length() > "任务变更内容:\n".length() ? changes.toString() : "无字段变更";
    }
}