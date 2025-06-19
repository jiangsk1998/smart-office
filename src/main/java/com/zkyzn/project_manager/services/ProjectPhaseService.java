package com.zkyzn.project_manager.services;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.zkyzn.project_manager.enums.TaskStatusEnum;
import com.zkyzn.project_manager.mappers.ProjectPhaseDao;
import com.zkyzn.project_manager.models.ProjectPhase;
import com.zkyzn.project_manager.models.ProjectPlan;
import com.zkyzn.project_manager.so.project.dashboard.PhaseProgress;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mr-ti
 * Date: 2025/6/10 17:59
 */
@Service
public class ProjectPhaseService extends MPJBaseServiceImpl<ProjectPhaseDao, ProjectPhase> {

    @Resource
    private ProjectPlanService projectPlanService;

    /**
     * 根据项目id删除项目阶段
     * @param projectId
     */
    public void removeByProjectId(Long projectId) {
        this.lambdaUpdate().eq(ProjectPhase::getProjectId, projectId).remove();
    }


    /**
     * 根据项目id获取项目阶段
     * @param projectId
     * @return
     */
    public List<ProjectPhase> listByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectPhase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPhase::getProjectId, projectId);
        return this.list(wrapper);
    }

    /**
     * 获取阶段进度
     * @param projectId
     * @return
     */

    public List<ProjectPhase> getPhasesByProjectId(Long projectId) {
        MPJLambdaQueryWrapper<ProjectPhase> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.selectAll(ProjectPhase.class)
                .eq(ProjectPhase::getProjectId, projectId)
                .orderByAsc(ProjectPhase::getStartDate);

        return baseMapper.selectList(wrapper);
    }

    /**
     * 获取阶段进度明细
     * @param projectId
     * @return
     */
    public List<PhaseProgress> getPhaseProgressDetails(Long projectId) {
        List<ProjectPhase> phases = getPhasesByProjectId(projectId);
        List<PhaseProgress> progressList = new ArrayList<>();

        for (ProjectPhase phase : phases) {
            String phaseName = phase.getPhaseName();

            // 获取该阶段的所有任务
            List<ProjectPlan> plans = projectPlanService.getPlansByPhase(projectId, phaseName);

            if (plans.isEmpty()) {
                continue;
            }

            // 计算计划进度和实际进度
            int totalTasks = plans.size();
            int completedTasks = 0;
            int shouldCompleted = 0;
            LocalDate today = LocalDate.now();

            for (ProjectPlan plan : plans) {
                if (TaskStatusEnum.COMPLETED.name().equals(plan.getTaskStatus())) {
                    completedTasks++;
                }
                if (plan.getEndDate().isBefore(today) || plan.getEndDate().isEqual(today)) {
                    shouldCompleted++;
                }
            }

            PhaseProgress progress = new PhaseProgress();
            progress.setPhaseName(phaseName);

            if (totalTasks > 0) {
                progress.setPlannedProgress(
                        BigDecimal.valueOf(shouldCompleted)
                                .divide(BigDecimal.valueOf(totalTasks), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                );
                progress.setActualProgress(
                        BigDecimal.valueOf(completedTasks)
                                .divide(BigDecimal.valueOf(totalTasks), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                );
            } else {
                progress.setPlannedProgress(BigDecimal.ZERO);
                progress.setActualProgress(BigDecimal.ZERO);
            }

            progressList.add(progress);
        }

        return progressList;
    }
}
