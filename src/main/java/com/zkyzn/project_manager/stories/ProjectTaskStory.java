package com.zkyzn.project_manager.stories;

import com.zkyzn.project_manager.models.*;
import com.zkyzn.project_manager.models.message.ChangeNoticeContent;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectTaskStory {

    // ============= 通知标题常量 =============
    private static final String NOTIFY_TITLE_CREATE = "新增项目任务通知";
    private static final String NOTIFY_TITLE_STATUS_CHANGE = "项目任务状态变更通知";
    private static final String NOTIFY_TITLE_UPDATE = "项目任务信息更新通知";
    private static final String NOTIFY_TITLE_DELETE = "项目任务删除通知";

    @Resource
    private ProjectInfoService projectInfoService;
    @Resource
    private MessageInfoStory messageInfoStory;
    @Resource
    private ProjectPlanService projectPlanService;

    // ============= 业务方法（添加事务和通知） =============

    @Transactional(rollbackFor = Exception.class)
    public Boolean createPlan(ProjectPlan projectPlan, Long operatorId) {
        if (!this.projectPlanService.save(projectPlan)) return false;

        ProjectInfo projectInfo = getProjectInfoSafely(projectPlan.getProjectId());
        if (projectInfo == null) return true; // 项目不存在不阻塞主流程

        return sendNotification(projectInfo,
                NOTIFY_TITLE_CREATE,
                buildCreateNoticeContent(projectPlan),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePlanById(ProjectPlan projectPlan, Long operatorId) {
        ProjectPlan originalPlan = this.projectPlanService.getById(projectPlan.getProjectPlanId());
        if (originalPlan == null) return false;

        if (!this.projectPlanService.updateById(projectPlan)) return false;

        ProjectInfo projectInfo = getProjectInfoSafely(projectPlan.getProjectId());
        if (projectInfo == null) return true;

        return sendNotification(projectInfo,
                NOTIFY_TITLE_UPDATE,
                buildUpdateNoticeContent(originalPlan, projectPlan),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean changePlanStatus(Long id, String status, Long operatorId) {
        ProjectPlan currentPlan = this.projectPlanService.getById(id);
        if (currentPlan == null) return false;
        if (status.equals(currentPlan.getTaskStatus())) return true; // 状态未变

        boolean success = this.projectPlanService.lambdaUpdate()
                .eq(ProjectPlan::getProjectPlanId, id)
                .set(ProjectPlan::getTaskStatus, status)
                .update();

        if (!success) return false;

        ProjectInfo projectInfo = getProjectInfoSafely(currentPlan.getProjectId());
        if (projectInfo == null) return false;

        return sendNotification(projectInfo,
                NOTIFY_TITLE_STATUS_CHANGE,
                String.format("任务状态已从 [%s] 更新为: [%s]",
                        currentPlan.getTaskStatus(), status),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlanById(Long id, Long operatorId) {
        ProjectPlan plan = this.projectPlanService.getById(id);
        if (plan == null) return false;

        boolean deleteSuccess = this.projectPlanService.lambdaUpdate()
                .eq(ProjectPlan::getProjectPlanId, id)
                .remove();

        if (!deleteSuccess) return false;

        ProjectInfo projectInfo = getProjectInfoSafely(plan.getProjectId());
        if (projectInfo == null) return true;

        return sendNotification(projectInfo,
                NOTIFY_TITLE_DELETE,
                String.format("任务 [%s] 已被删除", plan.getTaskDescription()),
                operatorId);
    }

    // ============= 通知辅助方法 =============

    private Boolean sendNotification(ProjectInfo projectInfo,
                                     String title,
                                     String contentText,
                                     Long operatorId) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSenderId(operatorId);
        messageInfo.setTitle(title);
        messageInfo.setMessageType(1); // 假设1为系统通知
        messageInfo.setContent(buildNoticeContent(projectInfo, contentText));
        messageInfo.setReadStatus(false);

        return messageInfoStory.sendMessages(messageInfo, getNotifyRecipients(projectInfo));
    }

    private ChangeNoticeContent buildNoticeContent(ProjectInfo projectInfo, String contentText) {
        ChangeNoticeContent content = new ChangeNoticeContent();
        content.setProjectNumber(projectInfo.getProjectNumber());
        content.setProjectName(projectInfo.getProjectName());
        content.setContent(contentText);
        return content;
    }

    private String buildCreateNoticeContent(ProjectPlan plan) {
        return String.format("新增任务: %s\n" +
                        "- 责任人: %s\n" +
                        "- 计划时间: %s 至 %s\n" +
                        "- 交付物: %s",
                formatValue(plan.getTaskDescription()),
                formatValue(plan.getResponsiblePerson()),
                formatDate(plan.getStartDate()),
                formatDate(plan.getEndDate()),
                formatValue(plan.getDeliverable()));
    }

    private String buildUpdateNoticeContent(ProjectPlan original, ProjectPlan updated) {
        StringBuilder changes = new StringBuilder("任务变更内容:\n");

        addChangeIfDifferent(changes, "任务描述", original.getTaskDescription(), updated.getTaskDescription());
        addChangeIfDifferent(changes, "责任人", original.getResponsiblePerson(), updated.getResponsiblePerson());
        addDateChangeIfDifferent(changes, "开始日期", original.getStartDate(), updated.getStartDate());
        addDateChangeIfDifferent(changes, "结束日期", original.getEndDate(), updated.getEndDate());
        addChangeIfDifferent(changes, "交付物", original.getDeliverable(), updated.getDeliverable());
        addChangeIfDifferent(changes, "科室", original.getDepartment(), updated.getDepartment());

        return changes.length() > 0 ? changes.toString() : "无字段变更";
    }

    private void addChangeIfDifferent(StringBuilder sb, String fieldName, String oldVal, String newVal) {
        if (!StringUtils.equals(oldVal, newVal)) {
            sb.append(String.format("- %s: [%s] → [%s]\n",
                    fieldName,
                    formatValue(oldVal),
                    formatValue(newVal)));
        }
    }

    private void addDateChangeIfDifferent(StringBuilder sb, String fieldName,
                                          LocalDate oldDate, LocalDate newDate) {
        if (!Objects.equals(oldDate, newDate)) {
            sb.append(String.format("- %s: [%s] → [%s]\n",
                    fieldName,
                    formatDate(oldDate),
                    formatDate(newDate)));
        }
    }

    // ============= 工具方法 =============

    private ProjectInfo getProjectInfoSafely(Long projectId) {
        return projectInfoService.getByProjectId(projectId);
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
}