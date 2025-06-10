package com.zkyzn.project_manager.stories;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.ProjectPlanDao;
import com.zkyzn.project_manager.models.ProjectPlan;
import org.springframework.stereotype.Service;

/**
 * Todo 状态级联变更 时间级联变更  变更通知
 * Date: 2025/6/8 23:02
 */
@Service
public class ProjectPlanStory extends MPJBaseServiceImpl<ProjectPlanDao, ProjectPlan> {

    public Boolean createPlan(ProjectPlan projectPlan) {
        return this.save(projectPlan);
    }

    public Boolean updatePlanById(ProjectPlan projectPlan) {
        return this.updateById(projectPlan);
    }

    public Boolean changePlanStatus(Long id, String status) {
        return this.lambdaUpdate().eq(ProjectPlan::getProjectPlanId, id).set(ProjectPlan::getTaskStatus, status).update();
    }

    public Boolean deletePlanById(Long id) {
        return this.lambdaUpdate().eq(ProjectPlan::getProjectPlanId, id).remove();
    }
}
