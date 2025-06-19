// src/main/java/com/zkyzn/project_manager/stories/ProjectPhaseStory.java
package com.zkyzn.project_manager.stories;


import com.zkyzn.project_manager.enums.PhaseStatusEnum;
import com.zkyzn.project_manager.enums.ProjectStatusEnum;
import com.zkyzn.project_manager.events.ProjectPhaseChangeEvent;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ProjectInfoService;
import com.zkyzn.project_manager.services.ProjectPhaseService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 负责处理与项目阶段相关的复杂业务逻辑的服务类。
 * 此类已通过事件发布机制与通知模块解耦。
 * @author Jiangsk
 */
@Service
public class ProjectPhaseStory {

    /**
     * 注入项目阶段服务，用于数据库操作。
     */
    @Resource
    private ProjectPhaseService projectPhaseService;

    /**
     * 注入项目信息服务，用于数据库操作。
     */
    @Resource
    private ProjectInfoService projectInfoService;

    /**
     * 注入Spring的事件发布器，用于在业务操作完成后发布变更事件。
     */
    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private ProjectPlanService projectPlanService;

    /**
     * 创建一个新的项目阶段。
     * 操作成功后会发布一个 "CREATE" 类型的阶段变更事件。
     * 这是一个事务性操作，如果任何步骤失败，所有数据库更改都将回滚。
     *
     * @param projectPhase 要创建的项目阶段对象。
     * @param operatorId   执行此操作的用户ID。
     * @return 如果创建成功返回 true，否则返回 false。
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean createPhase(ProjectPhase projectPhase, Long operatorId) {
        ZonedDateTime now = ZonedDateTime.now();
        projectPhase.setCreateTime(now);
        projectPhase.setUpdateTime(now);

        boolean success = projectPhaseService.save(projectPhase);
        if (success) {
            // 发布“创建”事件，将创建后的阶段信息传递出去
            ProjectPhaseChangeEvent event = new ProjectPhaseChangeEvent(this, operatorId, "CREATE", null, projectPhase);
            eventPublisher.publishEvent(event);
        }
        return success;
    }

    /**
     * 变更指定ID的项目阶段的状态。
     * 如果状态发生实际变化，将发布一个 "STATUS_CHANGE" 类型的阶段变更事件，并重新评估整个项目的状态。
     * 这是一个事务性操作。
     *
     * @param id         要变更状态的项目阶段的ID。
     * @param status     新的状态字符串。
     * @param operatorId 执行此操作的用户ID。
     * @return 如果变更成功返回 true，否则返回 false。
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePhaseStatusById(Long id, String status, Long operatorId) {
        ProjectPhase currentPhase = projectPhaseService.getById(id);
        if (currentPhase == null || status.equals(currentPhase.getPhaseStatus())) {
            // 如果阶段不存在或状态未改变，则不执行任何操作
            return false;
        }

        ProjectPhase updateEntity = new ProjectPhase();
        updateEntity.setPhaseId(id);
        updateEntity.setPhaseStatus(status);
        updateEntity.setUpdateTime(ZonedDateTime.now());

        boolean success = projectPhaseService.updateById(updateEntity);

        if (success) {
            // 重新获取更新后的完整对象
            ProjectPhase updatedPhase = projectPhaseService.getById(id);
            // 发布“状态变更”事件，包含变更前后的数据
            ProjectPhaseChangeEvent event = new ProjectPhaseChangeEvent(this, operatorId, "STATUS_CHANGE", currentPhase, updatedPhase);
            eventPublisher.publishEvent(event);

            // 触发项目状态分析
            analyzeProjectStatus(currentPhase.getProjectId());
        }
        return success;
    }

    /**
     * 根据ID删除一个项目阶段。
     * 删除成功后会发布一个 "DELETE" 类型的阶段变更事件。
     * 这是一个事务性操作。
     *
     * @param id         要删除的项目阶段的ID。
     * @param operatorId 执行此操作的用户ID。
     * @return 如果删除成功返回 true，否则返回 false。
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePhaseById(Long id, Long operatorId) {
        ProjectPhase phase = projectPhaseService.getById(id);
        if (phase == null) {
            return false;
        }

        boolean deleteSuccess = projectPhaseService.removeById(id);

        if (deleteSuccess) {
            // 发布“删除”事件，传递被删除的阶段信息
            ProjectPhaseChangeEvent event = new ProjectPhaseChangeEvent(this, operatorId, "DELETE", phase, null);
            eventPublisher.publishEvent(event);
        }
        return deleteSuccess;
    }

    /**
     * 根据ID更新一个项目阶段的信息。
     * 此方法不会更新阶段的状态。
     * 更新成功后会发布一个 "UPDATE" 类型的阶段变更事件。
     * 这是一个事务性操作。
     *
     * @param projectPhase 包含更新后信息的项目阶段对象。
     * @param operatorId   执行此操作的用户ID。
     * @return 如果更新成功返回 true，否则返回 false。
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePhaseById(ProjectPhase projectPhase, Long operatorId) {
        ProjectPhase originalPhase = projectPhaseService.getById(projectPhase.getPhaseId());
        if (originalPhase == null) {
            return false;
        }

        projectPhase.setPhaseStatus(null); // 确保状态不会在此方法中被意外修改
        projectPhase.setUpdateTime(ZonedDateTime.now());

        boolean success = projectPhaseService.updateById(projectPhase);

        // 级联修改子任务的任务包名称
        if (success && StringUtils.isNotBlank(projectPhase.getPhaseName())) {
            this.projectPlanService.lambdaUpdate().eq(ProjectPlan::getPhaseId,projectPhase.getPhaseId())
                    .set(ProjectPlan::getTaskPackage,projectPhase.getPhaseName()).update();
        }

        if (success) {
            // 发布“更新”事件，包含变更前后的数据
            ProjectPhaseChangeEvent event = new ProjectPhaseChangeEvent(this, operatorId, "UPDATE", originalPhase, projectPhase);
            eventPublisher.publishEvent(event);
        }
        return success;
    }


    /**
     * 分析并根据需要更新项目的整体状态。
     * 例如，如果一个项目的所有阶段都已完成，则将项目状态更新为“已完结”。
     *
     * @param projectId 要分析的项目ID。
     */
    private void analyzeProjectStatus(Long projectId) {
        // 1. 获取项目所有阶段
        List<ProjectPhase> phases = projectPhaseService.listByProjectId(projectId);

        // 2. 检查是否所有阶段都已完成
        boolean allCompleted = !phases.isEmpty() &&
                phases.stream().allMatch(phase -> PhaseStatusEnum.COMPLETED.name().equals(phase.getPhaseStatus()));

        // 3. 如果所有阶段都已完成，则更新项目状态为“已完结”
        if (allCompleted) {
            ProjectInfo projectUpdate = new ProjectInfo();
            projectUpdate.setProjectId(projectId);
            projectUpdate.setStatus(ProjectStatusEnum.COMPLETED.name());
            projectInfoService.updateById(projectUpdate);
        }
    }
}