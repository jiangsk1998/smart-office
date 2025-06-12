package com.zkyzn.project_manager.listener;

import com.zkyzn.project_manager.events.ProjectTaskChangeEvent;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.models.message.ChangeNoticeContent;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.stories.MessageInfoStory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class ProjectTaskChangeListener {

    @Resource
    private MessageInfoStory messageInfoStory;
    @Resource
    private ProjectInfoService projectInfoService;

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

        // 发送通知
        sendTaskNotification(projectInfo, title, contentText, operatorId, plan);
    }

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

    private Set<Long> getNotifyRecipients(ProjectInfo projectInfo) {
        Set<Long> recipients = new HashSet<>(3);
        Optional.ofNullable(projectInfo.getResponsibleLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getTechnicalLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getPlanSupervisorId()).ifPresent(recipients::add);
        return recipients;
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