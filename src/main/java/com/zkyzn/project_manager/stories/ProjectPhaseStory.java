package com.zkyzn.project_manager.stories;


import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.ProjectPhaseDao;
import com.zkyzn.project_manager.models.ProjectPhase;
import org.springframework.stereotype.Service;

/**
 * Todo 状态级联变更 时间级联变更 变更通知
 * Date: 2025/6/10 17:59
 */
@Service
public class ProjectPhaseStory extends MPJBaseServiceImpl<ProjectPhaseDao, ProjectPhase> {

    public Boolean createPhase(ProjectPhase projectPhase) {
        return this.save(projectPhase);
    }

    public Boolean updatePhaseById(ProjectPhase projectPhase) {
        return this.updateById(projectPhase);
    }

    public Boolean changePhaseStatus(Long id, String status) {
        return this.lambdaUpdate().eq(ProjectPhase::getPhaseId, id).set(ProjectPhase::getPhaseStatus, status).update();
    }

    public Boolean deletePhaseById(Long id) {
        return this.lambdaUpdate().eq(ProjectPhase::getPhaseId, id).remove();
    }
}
