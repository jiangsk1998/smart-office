package com.zkyzn.project_manager.stories;


import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.services.ProjectPhaseService;
import com.zkyzn.project_manager.utils.ProjectPhaseOrTaskChangeNoticeUtils;
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
    private ProjectPhaseOrTaskChangeNoticeUtils noticeUtils;

    @Transactional(rollbackFor = Exception.class)
    public Boolean createPhase(ProjectPhase projectPhase, Long operatorId) {
        if (!projectPhaseService.save(projectPhase)) {
            return false;
        }

        ProjectInfo projectInfo = noticeUtils.getProjectInfoSafely(projectPhase.getProjectId());
        if (projectInfo == null) {
            return true;
        }

        return noticeUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_CREATE,
                buildCreateNoticeContent(projectPhase),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean changePhaseStatusById(Long id, String status, Long operatorId) {
        ProjectPhase currentPhase = projectPhaseService.getById(id);
        if (currentPhase == null) return false;
        if (status.equals(currentPhase.getPhaseStatus())) return true;

        ProjectInfo projectInfo = noticeUtils.getProjectInfoSafely(currentPhase.getProjectId());
        if (projectInfo == null) return false;

        ProjectPhase updateEntity = new ProjectPhase();
        updateEntity.setPhaseId(id);
        updateEntity.setPhaseStatus(status);
        if (!projectPhaseService.updateById(updateEntity)) {
            return false;
        }

        return noticeUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_STATUS_CHANGE,
                String.format("项目阶段状态已从 [%s] 更新为: [%s]",
                        currentPhase.getPhaseStatus(), status),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePhaseById(Long id, Long operatorId) {
        ProjectPhase phase = projectPhaseService.getById(id);
        if (phase == null) return false;

        ProjectInfo projectInfo = noticeUtils.getProjectInfoSafely(phase.getProjectId());
        if (projectInfo == null) return false;

        boolean deleteSuccess = projectPhaseService.lambdaUpdate()
                .eq(ProjectPhase::getPhaseId, id)
                .remove();

        if (deleteSuccess) {
            noticeUtils.sendNotification(projectInfo,
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

        ProjectInfo projectInfo = noticeUtils.getProjectInfoSafely(originalPhase.getProjectId());
        if (projectInfo == null) return true;

        return noticeUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_UPDATE,
                buildUpdateNoticeContent(originalPhase, projectPhase),
                operatorId);
    }

    private String buildCreateNoticeContent(ProjectPhase phase) {
        return String.format("新增项目阶段 [%s]：\n- 负责人: %s\n- 计划时间: %s 至 %s\n- 交付物: %s",
                noticeUtils.formatValue(phase.getPhaseName()),
                noticeUtils.formatValue(phase.getResponsiblePerson()),
                noticeUtils.formatDate(phase.getStartDate()),
                noticeUtils.formatDate(phase.getEndDate()),
                noticeUtils.formatValue(phase.getDeliverable()));
    }

    private String buildUpdateNoticeContent(ProjectPhase original, ProjectPhase updated) {
        StringBuilder changes = new StringBuilder("项目阶段信息更新：\n");

        noticeUtils.addChangeIfDifferent(changes, "阶段名称", original.getPhaseName(), updated.getPhaseName());
        noticeUtils.addDateChangeIfDifferent(changes, "开始日期", original.getStartDate(), updated.getStartDate());
        noticeUtils.addDateChangeIfDifferent(changes, "结束日期", original.getEndDate(), updated.getEndDate());
        noticeUtils.addChangeIfDifferent(changes, "负责人", original.getResponsiblePerson(), updated.getResponsiblePerson());
        noticeUtils.addChangeIfDifferent(changes, "成果描述", original.getDeliverable(), updated.getDeliverable());
        noticeUtils.addChangeIfDifferent(changes, "成果类型", original.getDeliverableType(), updated.getDeliverableType());

        if (changes.toString().equals("项目阶段信息更新：\n")) {
            changes.append("无字段变更");
        }

        return changes.toString();
    }
}