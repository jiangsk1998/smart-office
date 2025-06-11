package com.zkyzn.project_manager.stories;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.ProjectPhaseDao;
import com.zkyzn.project_manager.models.MessageInfo;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.message.ChangeNoticeContent;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPhaseService;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Todo 状态级联变更 时间级联变更 变更通知
 * Date: 2025/6/10 17:59
 */
@Service
public class ProjectPhaseStory {

    //============= 通知标题常量 =============//
    private static final String NOTIFY_TITLE_CREATE = "新增项目阶段通知";
    private static final String NOTIFY_TITLE_STATUS_CHANGE = "项目阶段状态变更通知";
    private static final String NOTIFY_TITLE_UPDATE = "项目阶段信息更新通知";
    private static final String NOTIFY_TITLE_DELETE = "项目阶段删除通知";

    @Resource
    private ProjectInfoService projectInfoService;

    @Resource
    private MessageInfoStory messageInfoStory;

    @Resource
    private ProjectPhaseService projectPhaseService;

    //============= 业务方法 =============//

    @Transactional(rollbackFor = Exception.class)
    public Boolean createPhase(ProjectPhase projectPhase, Long operatorId) {
        if (!projectPhaseService.save(projectPhase)) {
            return false;
        }

        ProjectInfo projectInfo = getProjectInfoSafely(projectPhase.getProjectId());
        if (projectInfo == null) {
            return true;
        }

        return sendNotification(projectInfo,
                NOTIFY_TITLE_CREATE,
                buildCreateNoticeContent(projectPhase),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean changePhaseStatus(Long id, String status, Long operatorId) {
        ProjectPhase currentPhase = projectPhaseService.getById(id);
        if (currentPhase == null) return false;
        if (status.equals(currentPhase.getPhaseStatus())) return true;

        ProjectInfo projectInfo = getProjectInfoSafely(currentPhase.getProjectId());
        if (projectInfo == null) return false;

        ProjectPhase updateEntity = new ProjectPhase();
        updateEntity.setPhaseId(id);
        updateEntity.setPhaseStatus(status);
        if (!projectPhaseService.updateById(updateEntity)) {
            return false;
        }

        return sendNotification(projectInfo,
                NOTIFY_TITLE_STATUS_CHANGE,
                String.format("项目阶段状态已从 [%s] 更新为: [%s]",
                        currentPhase.getPhaseStatus(), status),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePhaseById(Long id, Long operatorId) {
        ProjectPhase phase = projectPhaseService.getById(id);
        if (phase == null) return false;

        ProjectInfo projectInfo = getProjectInfoSafely(phase.getProjectId());
        if (projectInfo == null) return false;

        boolean deleteSuccess = projectPhaseService.lambdaUpdate()
                .eq(ProjectPhase::getPhaseId, id)
                .remove();

        if (deleteSuccess) {
            sendNotification(projectInfo,
                    NOTIFY_TITLE_DELETE,
                    String.format("项目阶段 [%s] 已被永久删除", phase.getPhaseName()),
                    operatorId);
        }

        return deleteSuccess;
    }

    public Boolean updatePhaseById(ProjectPhase projectPhase, Long operatorId) {
        ProjectPhase originalPhase = projectPhaseService.getById(projectPhase.getPhaseId());
        if (originalPhase == null) return false;

        // 禁止修改状态字段
        projectPhase.setPhaseStatus(null);

        if (!projectPhaseService.updateById(projectPhase)) {
            return false;
        }

        ProjectInfo projectInfo = getProjectInfoSafely(originalPhase.getProjectId());
        if (projectInfo == null) return true;

        return sendNotification(projectInfo,
                NOTIFY_TITLE_UPDATE,
                buildUpdateNoticeContent(originalPhase, projectPhase),
                operatorId);
    }

    //============= 重构后的通知方法 =============//

    /**
     * 发送通知消息（统一入口）
     */
    private Boolean sendNotification(ProjectInfo projectInfo,
                                     String title,
                                     String contentText,
                                     Long operatorId) {
        // 创建消息对象
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSenderId(operatorId);
        messageInfo.setTitle(title);
        messageInfo.setMessageType(1);
        messageInfo.setContent(buildNoticeContent(projectInfo, contentText));
        messageInfo.setReadStatus(false);

        // 获取接收人并发送
        return messageInfoStory.sendMessages(messageInfo, getNotifyRecipients(projectInfo));
    }

    /**
     * 构建通知内容对象
     */
    private ChangeNoticeContent buildNoticeContent(ProjectInfo projectInfo, String contentText) {
        ChangeNoticeContent content = new ChangeNoticeContent();
        content.setProjectNumber(projectInfo.getProjectNumber());
        content.setProjectName(projectInfo.getProjectName());
        content.setStartDate(projectInfo.getStartDate());
        content.setEndDate(projectInfo.getEndDate());
        content.setContent(contentText);
        return content;
    }

    //============= 通知内容构建方法 =============//

    private String buildCreateNoticeContent(ProjectPhase phase) {
        return String.format("新增项目阶段 [%s]：" +
                        "\n- 负责人: %s" +
                        "\n- 计划时间: %s 至 %s" +
                        "\n- 交付物: %s",
                formatValue(phase.getPhaseName()),
                formatValue(phase.getResponsiblePerson()),
                formatDate(phase.getStartDate()),
                formatDate(phase.getEndDate()),
                formatValue(phase.getDeliverable()));
    }

    private String buildUpdateNoticeContent(ProjectPhase original, ProjectPhase updated) {
        StringBuilder changes = new StringBuilder("项目阶段信息更新：\n");

        // 使用辅助方法添加变更项
        addChangeIfDifferent(changes, "阶段名称", original.getPhaseName(), updated.getPhaseName());
        addDateChangeIfDifferent(changes, "开始日期", original.getStartDate(), updated.getStartDate());
        addDateChangeIfDifferent(changes, "结束日期", original.getEndDate(), updated.getEndDate());
        addChangeIfDifferent(changes, "负责人", original.getResponsiblePerson(), updated.getResponsiblePerson());
        addChangeIfDifferent(changes, "成果描述", original.getDeliverable(), updated.getDeliverable());
        addChangeIfDifferent(changes, "成果类型", original.getDeliverableType(), updated.getDeliverableType());

        if (changes.toString().equals("项目阶段信息更新：\n")) {
            changes.append("无字段变更");
        }

        return changes.toString();
    }

    /**
     * 添加变更项（通用字段）
     */
    private void addChangeIfDifferent(StringBuilder sb, String fieldName,
                                      String originalValue, String updatedValue) {
        if (!StringUtils.equals(originalValue, updatedValue)) {
            sb.append(String.format("- %s: [%s] → [%s]\n",
                    fieldName,
                    formatValue(originalValue),
                    formatValue(updatedValue)));
        }
    }

    /**
     * 添加日期变更项
     */
    private void addDateChangeIfDifferent(StringBuilder sb, String fieldName,
                                          LocalDate originalDate, LocalDate updatedDate) {
        if (!Objects.equals(originalDate, updatedDate)) {
            sb.append(String.format("- %s: [%s] → [%s]\n",
                    fieldName,
                    formatDate(originalDate),
                    formatDate(updatedDate)));
        }
    }

    //============= 辅助方法 =============//

    private ProjectInfo getProjectInfoSafely(Long projectId) {
        return projectInfoService.getByProjectId(projectId);
    }

    private String formatValue(String value) {
        return StringUtils.isNotBlank(value) ? value : "未设置";
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : "未指定";
    }

    private Set<Long> getNotifyRecipients(ProjectInfo projectInfo) {
        Set<Long> recipients = new HashSet<>(3);
        Optional.ofNullable(projectInfo.getResponsibleLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getTechnicalLeaderId()).ifPresent(recipients::add);
        Optional.ofNullable(projectInfo.getPlanSupervisorId()).ifPresent(recipients::add);
        return recipients;
    }
}
