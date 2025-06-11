package com.zkyzn.project_manager.utils;

import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.message.ChangeNoticeContent;
import com.zkyzn.project_manager.stories.MessageInfoStory;
import com.zkyzn.project_manager.services.ProjectInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class NotificationUtils {

    private final ProjectInfoService projectInfoService;
    private final MessageInfoStory messageInfoStory;

    public NotificationUtils(ProjectInfoService projectInfoService,
                             MessageInfoStory messageInfoStory) {
        this.projectInfoService = projectInfoService;
        this.messageInfoStory = messageInfoStory;
    }

    public Boolean sendNotification(ProjectInfo projectInfo,
                                   String title,
                                   String contentText,
                                   Long operatorId) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSenderId(operatorId);
        messageInfo.setTitle(title);
        messageInfo.setMessageType(1);
        messageInfo.setContent(buildNoticeContent(projectInfo, contentText));
        messageInfo.setReadStatus(false);
        return messageInfoStory.sendMessages(messageInfo, getNotifyRecipients(projectInfo));
    }

    public ChangeNoticeContent buildNoticeContent(ProjectInfo projectInfo, String contentText) {
        ChangeNoticeContent content = new ChangeNoticeContent();
        content.setProjectNumber(projectInfo.getProjectNumber());
        content.setProjectName(projectInfo.getProjectName());
        content.setContent(contentText);
        return content;
    }

    public ProjectInfo getProjectInfoSafely(Long projectId) {
        return projectInfoService.getByProjectId(projectId);
    }

    public Set<Long> getNotifyRecipients(ProjectInfo projectInfo) {
        Set<Long> recipients = new HashSet<>(3);
        Optional.ofNullable(projectInfo.getResponsibleLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getTechnicalLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getPlanSupervisorId()).ifPresent(recipients::add);
        return recipients;
    }

    public String formatValue(String value) {
        return StringUtils.isNotBlank(value) ? value : "未设置";
    }

    public String formatDate(LocalDate date) {
        return date != null ? date.toString() : "未指定";
    }

    public void addChangeIfDifferent(StringBuilder sb, String fieldName, 
                                    String oldVal, String newVal) {
        if (!StringUtils.equals(oldVal, newVal)) {
            sb.append(String.format("- %s: [%s] → [%s]\n",
                    fieldName,
                    formatValue(oldVal),
                    formatValue(newVal)));
        }
    }

    public void addDateChangeIfDifferent(StringBuilder sb, String fieldName,
                                        LocalDate oldDate, LocalDate newDate) {
        if (!Objects.equals(oldDate, newDate)) {
            sb.append(String.format("- %s: [%s] → [%s]\n",
                    fieldName,
                    formatDate(oldDate),
                    formatDate(newDate)));
        }
    }
}