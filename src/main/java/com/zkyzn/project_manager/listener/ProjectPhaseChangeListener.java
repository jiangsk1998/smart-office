package com.zkyzn.project_manager.listener;

import com.zkyzn.project_manager.events.ProjectPhaseChangeEvent;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
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
public class ProjectPhaseChangeListener {

    @Resource
    private MessageInfoStory messageInfoStory;
    @Resource
    private ProjectInfoService projectInfoService;

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
        Set<Long> recipients = new HashSet<>(3);
        Optional.ofNullable(projectInfo.getResponsibleLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getTechnicalLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getPlanSupervisorId()).ifPresent(recipients::add);
        return recipients;
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
        // 其他字段比较...
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
}