package com.zkyzn.project_manager.services;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.zkyzn.project_manager.mappers.ProjectPhaseDao;
import com.zkyzn.project_manager.models.ProjectPhase;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/10 17:59
 */
@Service
public class ProjectPhaseService extends MPJBaseServiceImpl<ProjectPhaseDao, ProjectPhase> {

    /**
     * 根据项目id删除项目阶段
     * @param projectId
     */
    public void removeByProjectId(Long projectId) {
        this.lambdaUpdate().eq(ProjectPhase::getProjectId, projectId).remove();
    }


    public List<ProjectPhase> listByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectPhase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPhase::getProjectId, projectId);
        return this.list(wrapper);
    }
}
