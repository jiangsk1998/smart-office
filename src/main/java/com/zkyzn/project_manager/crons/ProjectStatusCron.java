package com.zkyzn.project_manager.crons;

import com.zkyzn.project_manager.enums.ProjectStatusEnum;
import com.zkyzn.project_manager.models.ProjectInfo;
import com.zkyzn.project_manager.services.ProjectInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 项目状态定时任务
 *
 * @author Zhang Fan
 */
@Slf4j
@Component
public class ProjectStatusCron {

    @Autowired
    private ProjectInfoService projectInfoService;

    /**
     * 每天1点执行，变更今天启动的项目从未开始到进行中状态和昨天未完成的项目到超时状态
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkAndChangeTask() {
        log.info("定时任务：开始变更项目状态...");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 处理所有未开始的项目
        List<ProjectInfo> notStartedProjects = projectInfoService.findByStatus(ProjectStatusEnum.NOT_STARTED.name());
        List<ProjectInfo> toInProgressProjects = notStartedProjects.stream()
                .filter(projectInfo -> projectInfo.getStartDate().isAfter(yesterday))
                .peek(projectInfo -> projectInfo.setStatus(ProjectStatusEnum.IN_PROGRESS.name()))
                .toList();
        if (!toInProgressProjects.isEmpty()) {
            projectInfoService.updateBatchById(toInProgressProjects);
        }

        // 处理所有超期的项目
        List<ProjectInfo> inProgressProjects = projectInfoService.findByStatus(ProjectStatusEnum.IN_PROGRESS.name());
        List<ProjectInfo> toOverdueProjects = inProgressProjects.stream()
                .filter(projectInfo -> today.isAfter(projectInfo.getEndDate()))
                .peek(projectInfo -> projectInfo.setStatus(ProjectStatusEnum.OVERDUE.name()))
                .toList();
        if (!toOverdueProjects.isEmpty()) {
            projectInfoService.updateBatchById(toOverdueProjects);
        }

        log.info("定时任务：变更项目状态完成，时间：{}，未开始——>进行中共{}个，进行中——>超时共{}个",
                dateFormatter.format(today), toInProgressProjects.size(), toOverdueProjects.size());
    }
}
