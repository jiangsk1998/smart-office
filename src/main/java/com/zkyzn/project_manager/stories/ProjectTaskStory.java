package com.zkyzn.project_manager.stories;

import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import com.zkyzn.project_manager.utils.ProjectPhaseOrTaskChangeNoticeUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;


@Service
public class ProjectTaskStory {

    private static final String NOTIFY_TITLE_CREATE = "新增项目任务通知";
    private static final String NOTIFY_TITLE_STATUS_CHANGE = "项目任务状态变更通知";
    private static final String NOTIFY_TITLE_UPDATE = "项目任务信息更新通知";
    private static final String NOTIFY_TITLE_DELETE = "项目任务删除通知";

    @Resource
    private ProjectPlanService projectPlanService;
    @Resource
    private ProjectPhaseOrTaskChangeNoticeUtils noticeUtils;

    @Transactional(rollbackFor = Exception.class)
    public Boolean createPlan(ProjectPlan projectPlan, Long operatorId) {
        if (!projectPlanService.save(projectPlan)) return false;

        LocalDateTime now = LocalDateTime.now();
        projectPlan.setCreateTime(now);
        projectPlan.setUpdateTime(now);

        ProjectInfo projectInfo = noticeUtils.getProjectInfoSafely(projectPlan.getProjectId());
        if (projectInfo == null) return true;

        return noticeUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_CREATE,
                buildCreateNoticeContent(projectPlan),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePlanById(ProjectPlan projectPlan, Long operatorId) {
        ProjectPlan originalPlan = projectPlanService.getById(projectPlan.getProjectPlanId());
        if (originalPlan == null) return false;

        projectPlan.setTaskStatus(null);
        projectPlan.setUpdateTime(LocalDateTime.now());

        if (!projectPlanService.updateById(projectPlan)) return false;

        ProjectInfo projectInfo = noticeUtils.getProjectInfoSafely(projectPlan.getProjectId());
        if (projectInfo == null) return true;

        return noticeUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_UPDATE,
                buildUpdateNoticeContent(originalPlan, projectPlan),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean changePlanStatus(Long id, String status, Long operatorId) {
        ProjectPlan currentPlan = projectPlanService.getById(id);
        if (currentPlan == null) return false;
        if (status.equals(currentPlan.getTaskStatus())) return true;

        boolean success = projectPlanService.lambdaUpdate()
                .eq(ProjectPlan::getProjectPlanId, id)
                .set(ProjectPlan::getTaskStatus, status)
                .set(ProjectPlan::getUpdateTime, ZonedDateTime.now())
                .update();

        if (!success) return false;

        ProjectInfo projectInfo = noticeUtils.getProjectInfoSafely(currentPlan.getProjectId());
        if (projectInfo == null) return false;

        return noticeUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_STATUS_CHANGE,
                String.format("任务状态已从 [%s] 更新为: [%s]",
                        currentPlan.getTaskStatus(), status),
                operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlanById(Long id, Long operatorId) {
        ProjectPlan plan = projectPlanService.getById(id);
        if (plan == null) return false;

        boolean deleteSuccess = projectPlanService.lambdaUpdate()
                .eq(ProjectPlan::getProjectPlanId, id)
                .remove();

        if (!deleteSuccess) return false;

        ProjectInfo projectInfo = noticeUtils.getProjectInfoSafely(plan.getProjectId());
        if (projectInfo == null) return true;

        return noticeUtils.sendNotification(projectInfo,
                NOTIFY_TITLE_DELETE,
                String.format("任务 [%s] 已被删除", plan.getTaskDescription()),
                operatorId);
    }

    private String buildCreateNoticeContent(ProjectPlan plan) {
        return String.format("新增任务: %s\n- 责任人: %s\n- 计划时间: %s 至 %s\n- 交付物: %s",
                noticeUtils.formatValue(plan.getTaskDescription()),
                noticeUtils.formatValue(plan.getResponsiblePerson()),
                noticeUtils.formatDate(plan.getStartDate()),
                noticeUtils.formatDate(plan.getEndDate()),
                noticeUtils.formatValue(plan.getDeliverable()));
    }

    private String buildUpdateNoticeContent(ProjectPlan original, ProjectPlan updated) {
        StringBuilder changes = new StringBuilder("任务变更内容:\n");

        noticeUtils.addChangeIfDifferent(changes, "任务描述", original.getTaskDescription(), updated.getTaskDescription());
        noticeUtils.addChangeIfDifferent(changes, "责任人", original.getResponsiblePerson(), updated.getResponsiblePerson());
        noticeUtils.addDateChangeIfDifferent(changes, "开始日期", original.getStartDate(), updated.getStartDate());
        noticeUtils.addDateChangeIfDifferent(changes, "结束日期", original.getEndDate(), updated.getEndDate());
        noticeUtils.addChangeIfDifferent(changes, "交付物", original.getDeliverable(), updated.getDeliverable());
        noticeUtils.addChangeIfDifferent(changes, "科室", original.getDepartment(), updated.getDepartment());

        return changes.length() > 0 ? changes.toString() : "无字段变更";
    }
}