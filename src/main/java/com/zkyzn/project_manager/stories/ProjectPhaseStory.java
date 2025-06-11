package com.zkyzn.project_manager.stories;


import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.services.ProjectPhaseService;
import com.zkyzn.project_manager.utils.NotificationUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectPhaseStory {

    private static final String NOTIFY_TITLE_CREATE = "新增项目阶段通知";
    private static final String NOTIFY_TITLE_STATUS_CHANGE = "项目阶段状态变更通知";
    private static final String NOTIFY_TITLE_UPDATE = "项目阶段信息更新通知";
    private static final String NOTIFY_TITLE_DELETE = "项目阶段删除通知";

    @Resource
    private ProjectPhaseService projectPhaseService;
    @Resource
    private NotificationUtils notificationUtils;

    @Transactional(rollbackFor = Exception.class)
    public Boolean createPhase(ProjectPhase projectPhase, Long operatorId) {
        if (!projectPhaseService.save(projectPhase)) {
            return false;
        }

        ProjectInfo projectInfo = notificationUtils.getProjectInfoSafely(projectPhase.getProjectId());
        if (projectInfo == null) {
            return true;
        }

        return notificationUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_CREATE,
                buildCreateNoticeContent(projectPhase),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean changePhaseStatusById(Long id, String status, Long operatorId) {
        ProjectPhase currentPhase = projectPhaseService.getById(id);
        if (currentPhase == null) return false;
        if (status.equals(currentPhase.getPhaseStatus())) return true;

        ProjectInfo projectInfo = notificationUtils.getProjectInfoSafely(currentPhase.getProjectId());
        if (projectInfo == null) return false;

        ProjectPhase updateEntity = new ProjectPhase();
        updateEntity.setPhaseId(id);
        updateEntity.setPhaseStatus(status);
        if (!projectPhaseService.updateById(updateEntity)) {
            return false;
        }

        return notificationUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_STATUS_CHANGE,
                String.format("项目阶段状态已从 [%s] 更新为: [%s]",
                        currentPhase.getPhaseStatus(), status),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePhaseById(Long id, Long operatorId) {
        ProjectPhase phase = projectPhaseService.getById(id);
        if (phase == null) return false;

        ProjectInfo projectInfo = notificationUtils.getProjectInfoSafely(phase.getProjectId());
        if (projectInfo == null) return false;

        boolean deleteSuccess = projectPhaseService.lambdaUpdate()
                .eq(ProjectPhase::getPhaseId, id)
                .remove();

        if (deleteSuccess) {
            notificationUtils.sendNotification(projectInfo,
                    NOTIFY_TITLE_DELETE,
                    String.format("项目阶段 [%s] 已被永久删除", phase.getPhaseName()),
                    operatorId);
        }

        return deleteSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePhaseById(ProjectPhase projectPhase, Long operatorId) {
        ProjectPhase originalPhase = projectPhaseService.getById(projectPhase.getPhaseId());
        if (originalPhase == null) return false;

        projectPhase.setPhaseStatus(null);

        if (!projectPhaseService.updateById(projectPhase)) {
            return false;
        }

        ProjectInfo projectInfo = notificationUtils.getProjectInfoSafely(originalPhase.getProjectId());
        if (projectInfo == null) return true;

        return notificationUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_UPDATE,
                buildUpdateNoticeContent(originalPhase, projectPhase),
                operatorId);
    }

    private String buildCreateNoticeContent(ProjectPhase phase) {
        return String.format("新增项目阶段 [%s]：\n- 负责人: %s\n- 计划时间: %s 至 %s\n- 交付物: %s",
                notificationUtils.formatValue(phase.getPhaseName()),
                notificationUtils.formatValue(phase.getResponsiblePerson()),
                notificationUtils.formatDate(phase.getStartDate()),
                notificationUtils.formatDate(phase.getEndDate()),
                notificationUtils.formatValue(phase.getDeliverable()));
    }

    private String buildUpdateNoticeContent(ProjectPhase original, ProjectPhase updated) {
        StringBuilder changes = new StringBuilder("项目阶段信息更新：\n");

        notificationUtils.addChangeIfDifferent(changes, "阶段名称", original.getPhaseName(), updated.getPhaseName());
        notificationUtils.addDateChangeIfDifferent(changes, "开始日期", original.getStartDate(), updated.getStartDate());
        notificationUtils.addDateChangeIfDifferent(changes, "结束日期", original.getEndDate(), updated.getEndDate());
        notificationUtils.addChangeIfDifferent(changes, "负责人", original.getResponsiblePerson(), updated.getResponsiblePerson());
        notificationUtils.addChangeIfDifferent(changes, "成果描述", original.getDeliverable(), updated.getDeliverable());
        notificationUtils.addChangeIfDifferent(changes, "成果类型", original.getDeliverableType(), updated.getDeliverableType());

        if (changes.toString().equals("项目阶段信息更新：\n")) {
            changes.append("无字段变更");
        }

        return changes.toString();
    }
}