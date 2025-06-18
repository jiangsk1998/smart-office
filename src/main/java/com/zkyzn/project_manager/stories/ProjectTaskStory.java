// src/main/java/com/zkyzn/project_manager/stories/ProjectTaskStory.java
package com.zkyzn.project_manager.stories;

import com.zkyzn.project_manager.events.ProjectTaskChangeEvent;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.services.ProjectPhaseService;
import com.zkyzn.project_manager.services.ProjectPlanService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**
 * 负责处理与项目任务（计划）相关的复杂业务逻辑的服务类。
 * "Story" 层用于编排一个或多个 "Service" 层操作以完成一个完整的业务流程。
 * 此类已通过事件发布机制与通知模块解耦。
 */
@Service
public class ProjectTaskStory {

    /**
     * 注入项目计划服务，用于数据库操作。
     */
    @Resource
    private ProjectPlanService projectPlanService;
    
    /**
     * 注入Spring的事件发布器，用于在业务操作完成后发布变更事件。
     */
    @Resource
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ProjectPhaseService projectPhaseService;

    /**
     * 创建一个新的项目任务。
     * 操作成功后会发布一个 "CREATE" 类型的任务变更事件。
     * 这是一个事务性操作，确保数据一致性。
     *
     * @param projectPlan 要创建的项目任务对象。
     * @param operatorId  执行此操作的用户ID。
     * @return 如果创建成功返回 true，否则返回 false。
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean createPlan(ProjectPlan projectPlan, Long operatorId) {
        LocalDateTime now = LocalDateTime.now();
        projectPlan.setCreateTime(now);
        projectPlan.setUpdateTime(now);
        projectPlan.setTaskPackage(projectPhaseService.getById(projectPlan.getPhaseId()).getPhaseName());

        boolean success = projectPlanService.save(projectPlan);
        if (success) {
            // 发布“创建”事件，通知其他模块（如通知服务）
            ProjectTaskChangeEvent event = new ProjectTaskChangeEvent(this, operatorId, "CREATE", null, projectPlan);
            eventPublisher.publishEvent(event);
        }
        return success;
    }

    /**
     * 根据ID更新一个项目任务的信息。
     * 不允许通过此方法更新任务状态。
     * 更新成功后会发布一个 "UPDATE" 类型的任务变更事件。
     * 这是一个事务性操作。
     *
     * @param projectPlan 包含更新后信息的项目任务对象。
     * @param operatorId  执行此操作的用户ID。
     * @return 如果更新成功返回 true，否则返回 false。
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePlanById(ProjectPlan projectPlan, Long operatorId) {
        ProjectPlan originalPlan = projectPlanService.getById(projectPlan.getProjectPlanId());
        if (originalPlan == null) {
            return false;
        }

        projectPlan.setTaskStatus(null); // 确保状态不会在此方法中被意外修改
        projectPlan.setUpdateTime(LocalDateTime.now());

        boolean success = projectPlanService.updateById(projectPlan);
        if (success) {
            // 发布“更新”事件，同时传递新旧两个版本的数据
            ProjectTaskChangeEvent event = new ProjectTaskChangeEvent(this, operatorId, "UPDATE", originalPlan, projectPlan);
            eventPublisher.publishEvent(event);
        }
        return success;
    }

    /**
     * 变更指定ID的项目任务的状态。
     * 如果状态发生实际变化，将发布一个 "STATUS_CHANGE" 类型的任务变更事件。
     * 这是一个事务性操作。
     *
     * @param id     要变更状态的项目任务的ID。
     * @param status 新的状态字符串。
     * @param operatorId 执行此操作的用户ID。
     * @return 如果变更成功返回 true，否则返回 false。
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePlanStatus(Long id, String status, Long operatorId) {
        ProjectPlan currentPlan = projectPlanService.getById(id);
        if (currentPlan == null || status.equals(currentPlan.getTaskStatus())) {
            return false; // 如果任务不存在或状态未改变，则不执行操作
        }
        
        // 创建一个仅包含要更新字段的实体，以提高效率
        ProjectPlan updateEntity = new ProjectPlan();
        updateEntity.setProjectPlanId(id);
        updateEntity.setTaskStatus(status);
        updateEntity.setUpdateTime(LocalDateTime.now());
        boolean success = projectPlanService.updateById(updateEntity);

        updateEntity.setProjectId(currentPlan.getProjectId());

        if (success) {
             // 发布“状态变更”事件
            ProjectTaskChangeEvent event = new ProjectTaskChangeEvent(this, operatorId, "STATUS_CHANGE", currentPlan, updateEntity);
            eventPublisher.publishEvent(event);
        }
        return success;
    }

    /**
     * 根据ID删除一个项目任务。
     * 删除成功后会发布一个 "DELETE" 类型的任务变更事件。
     * 这是一个事务性操作。
     *
     * @param id         要删除的项目任务的ID。
     * @param operatorId 执行此操作的用户ID。
     * @return 如果删除成功返回 true，否则返回 false。
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlanById(Long id, Long operatorId) {
        ProjectPlan plan = projectPlanService.getById(id);
        if (plan == null) {
            return false;
        }

        boolean deleteSuccess = projectPlanService.removeById(id);

        if (deleteSuccess) {
            // 发布“删除”事件
            ProjectTaskChangeEvent event = new ProjectTaskChangeEvent(this, operatorId, "DELETE", plan, null);
            eventPublisher.publishEvent(event);
        }
        return deleteSuccess;
    }
}